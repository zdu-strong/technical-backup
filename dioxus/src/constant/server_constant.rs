use crate::model::user_model::UserModel;
use chrono::Local;
use dioxus::prelude::*;
use dioxus_core::Task;
use dioxus_sdk::storage::new_persistent;
use futures::Future;
use futures::FutureExt;
use reqwest::Client;
use reqwest::Method;
use reqwest::RequestBuilder;
use reqwest::Url;
use std::panic::UnwindSafe;
use uuid::Uuid;

pub const SERVER_ADDRESS: GlobalSignal<String> = GlobalSignal::new(|| get_server_address());

pub const SERVER_USER_INFO: GlobalSignal<UserModel> = GlobalSignal::new(|| UserModel::default());

pub const KEY_OF_SERVER_USER_INFO_OF_PERSISTENT: &str =
    "GlobalUserInfo-5a8dcb9f-495f-4894-8dcb-9f495f489409";

pub const SERVER_ERROR: GlobalSignal<Option<String>> = GlobalSignal::new(|| None);

pub fn remove_server_user_info() {
    spawn_forever_global_call(move || {
        *SERVER_USER_INFO.write() = UserModel::default();
        if !get_server_user_info_persistent().is_empty() {
            set_server_user_info_persistent("".to_string());
        }
    });
}

pub fn set_server_user_info(user: Option<Signal<UserModel>>) {
    let mut user_json_string = serde_json::to_string(&user).unwrap().to_string();
    let has_params = !user.unwrap_or_default().read().access_token.is_empty();
    if !has_params {
        user_json_string = get_server_user_info_persistent();
        if user_json_string.is_empty() {
            remove_server_user_info();
            return;
        }
    }
    spawn_forever_global_call(move || {
        let user: UserModel = serde_json::from_str(user_json_string.as_str()).unwrap();
        *SERVER_USER_INFO.write() = user;
        if has_params {
            set_server_user_info_persistent(user_json_string);
        }
    });
}

pub fn get(url: &str) -> RequestBuilder {
    get_request_builder(Method::GET, url)
}

pub fn post(url: &str) -> RequestBuilder {
    get_request_builder(Method::POST, url)
}

pub fn put(url: &str) -> RequestBuilder {
    get_request_builder(Method::PUT, url)
}

pub fn delete(url: &str) -> RequestBuilder {
    get_request_builder(Method::DELETE, url)
}

pub fn use_multiple_query<F>(
    mut future: impl FnMut() -> F + UnwindSafe + 'static,
) -> HookStatusModel
where
    F: Future + UnwindSafe + 'static,
{
    let mut hook_status = use_signal(|| HookStatusModel::default());

    let callback = use_callback(move |_| {
        let fut = future();
        dioxus_core::spawn(async move {
            {
                *hook_status.write().loading.write() = true;
            }
            match fut.catch_unwind().await {
                Ok(_) => {
                    *hook_status.write().ready.write() = true;
                    *hook_status.write().loading.write() = false;
                    *hook_status.write().error.write() = None;
                }
                Err(_) => {
                    *hook_status.write().loading.write() = false;
                    *hook_status.write().error.write() = Some("Problem".to_string());
                }
            }
        })
    });

    // Create the task inside a CopyValue so we can reset it in-place later
    let task = use_hook(|| CopyValue::new(callback(())));

    // Early returns in dioxus have consequences for use_memo, use_resource, and use_future, etc
    // We *don't* want futures to be running if the component early returns. It's a rather weird behavior to have
    // use_memo running in the background even if the component isn't hitting those hooks anymore.
    //
    // React solves this by simply not having early returns interleave with hooks.
    // However, since dioxus allows early returns (since we use them for suspense), we need to solve this problem
    use_hook_did_run(move |did_run| match did_run {
        true => task.peek().resume(),
        false => task.peek().pause(),
    });

    let ready = hook_status.read().ready.clone();
    let loading = hook_status.read().loading.clone();
    let error = hook_status.read().error.clone();

    HookStatusModel {
        ready: ready,
        loading: loading,
        error: error,
        hook_callback: Some(callback),
    }
}

pub fn use_multiple_submit<F>(
    mut future: impl FnMut() -> F + UnwindSafe + 'static,
) -> HookStatusModel
where
    F: Future + UnwindSafe + 'static,
{
    let mut hook_status = use_signal(|| HookStatusModel::default());

    let callback = use_callback(move |_: ()| {
        let fut = future();
        dioxus_core::spawn(async move {
            if *hook_status.read().loading.read() {
                return;
            }
            {
                *hook_status.write().loading.write() = true;
            }
            match fut.catch_unwind().await {
                Ok(_) => {
                    *hook_status.write().ready.write() = true;
                    *hook_status.write().loading.write() = false;
                    *hook_status.write().error.write() = None;
                }
                Err(_) => {
                    *SERVER_ERROR.write() = Some("Problem".to_string());
                    *hook_status.write().loading.write() = false;
                    *hook_status.write().error.write() = Some("Problem".to_string());
                }
            }
        })
    });

    let ready = hook_status.read().ready.clone();
    let loading = hook_status.read().loading.clone();
    let error = hook_status.read().error.clone();

    HookStatusModel {
        ready: ready,
        loading: loading,
        error: error,
        hook_callback: Some(callback),
    }
}

pub fn use_once_submit<F>(mut future: impl FnMut() -> F + UnwindSafe + 'static) -> HookStatusModel
where
    F: Future + UnwindSafe + 'static,
{
    let mut hook_status = use_signal(|| HookStatusModel::default());

    let callback = use_callback(move |_: ()| {
        let fut = future();
        dioxus_core::spawn(async move {
            if *hook_status.read().loading.read() {
                return;
            }
            {
                *hook_status.write().loading.write() = true;
            }
            match fut.catch_unwind().await {
                Ok(_) => {
                    *hook_status.write().ready.write() = true;
                    *hook_status.write().loading.write() = true;
                    *hook_status.write().error.write() = None;
                }
                Err(_) => {
                    *SERVER_ERROR.write() = Some("Problem".to_string());
                    *hook_status.write().loading.write() = false;
                    *hook_status.write().error.write() = Some("Problem".to_string());
                }
            }
        })
    });

    let ready = hook_status.read().ready.clone();
    let loading = hook_status.read().loading.clone();
    let error = hook_status.read().error.clone();

    HookStatusModel {
        ready: ready,
        loading: loading,
        error: error,
        hook_callback: Some(callback),
    }
}

pub fn use_once_submit_while_true<F>(
    mut future: impl FnMut() -> F + UnwindSafe + 'static,
) -> HookStatusModel
where
    F: Future<Output = bool> + UnwindSafe + 'static,
{
    let mut hook_status = use_signal(|| HookStatusModel::default());

    let callback = use_callback(move |_: ()| {
        let fut = future();
        dioxus_core::spawn(async move {
            if *hook_status.read().loading.read() {
                return;
            }
            {
                *hook_status.write().loading.write() = true;
            }
            match fut.catch_unwind().await {
                Ok(result) => {
                    if result {
                        *hook_status.write().ready.write() = true;
                        *hook_status.write().loading.write() = true;
                        *hook_status.write().error.write() = None;
                    } else {
                        *hook_status.write().ready.write() = false;
                        *hook_status.write().loading.write() = false;
                        *hook_status.write().error.write() = None;
                    }
                }
                Err(_) => {
                    *SERVER_ERROR.write() = Some("Problem".to_string());
                    *hook_status.write().loading.write() = false;
                    *hook_status.write().error.write() = Some("Problem".to_string());
                }
            }
        })
    });

    let ready = hook_status.read().ready.clone();
    let loading = hook_status.read().loading.clone();
    let error = hook_status.read().error.clone();

    HookStatusModel {
        ready: ready,
        loading: loading,
        error: error,
        hook_callback: Some(callback),
    }
}

fn get_request_builder(method: Method, url: &str) -> RequestBuilder {
    let server_address = SERVER_ADDRESS.read().clone();
    let server_url = get_server_api_url(url, server_address.as_str());
    let mut request_builder = Client::new().request(method.clone(), server_url.clone());
    if server_url.origin().ascii_serialization()
        == Url::parse(server_address.as_str())
            .unwrap()
            .origin()
            .ascii_serialization()
    {
        let access_token = SERVER_USER_INFO.read().access_token.read().clone();
        if !access_token.is_empty() {
            request_builder = request_builder.bearer_auth(access_token);
        }
        if Method::GET != method {
            request_builder = request_builder.header("X-Nonce", Uuid::new_v4().to_string());
            request_builder = request_builder.header(
                "X-Timestamp",
                serde_json::to_string(&Local::now())
                    .unwrap()
                    .replace("\"", ""),
            );
        }
    }

    return request_builder;
}

fn get_server_api_url(url: &str, server_address: &str) -> Url {
    Url::options()
        .base_url(Option::Some(&Url::parse(server_address).unwrap()))
        .parse(url)
        .unwrap()
}

fn spawn_forever_global_call<F>(fut: F)
where
    F: FnOnce() -> () + Send + 'static,
{
    let task = dioxus::core::spawn_forever(async move {
        fut();
    })
    .poll_now();
    while !task.is_ready() {}
}

fn get_server_user_info_persistent() -> String {
    let user_info_persistent =
        new_persistent(KEY_OF_SERVER_USER_INFO_OF_PERSISTENT, || "".to_string());
    return user_info_persistent.read().clone();
}

fn set_server_user_info_persistent(user_json_string: String) {
    let mut user_info_persistent =
        new_persistent(KEY_OF_SERVER_USER_INFO_OF_PERSISTENT, || "".to_string());
    *user_info_persistent.write() = user_json_string;
}

fn get_server_address() -> String {
    "http://localhost:8080".to_string()
}

#[derive(Debug, Clone, Default)]
pub struct HookStatusModel {
    pub loading: Signal<bool>,
    pub ready: Signal<bool>,
    pub error: Signal<Option<String>>,
    pub hook_callback: Option<Callback<(), Task>>,
}

impl HookStatusModel {
    pub fn restart(self) {
        if self.hook_callback.is_some() {
            self.hook_callback.unwrap().call(());
        }
    }
}
