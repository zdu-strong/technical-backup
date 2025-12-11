use crate::model::user_model::UserModel;
use chrono::Local;
use dioxus::prelude::*;
use dioxus_sdk::storage::new_persistent;
use reqwest::Client;
use reqwest::Method;
use reqwest::RequestBuilder;
use reqwest::Url;
use uuid::Uuid;

pub const SERVER_ADDRESS: GlobalSignal<String> =
    GlobalSignal::new(|| "http://localhost:8080".to_string());

pub const SERVER_USER_INFO: GlobalSignal<UserModel> = GlobalSignal::new(|| UserModel::default());

pub const KEY_OF_SERVER_USER_INFO_OF_PERSISTENT: &str =
    "GlobalUserInfo-5a8dcb9f-495f-4894-8dcb-9f495f489409";

pub fn remove_server_user_info() {
    spawn_forever_global_call(move || {
        *SERVER_USER_INFO.write() = UserModel::default();
        let mut user_info_persistent =
            new_persistent(KEY_OF_SERVER_USER_INFO_OF_PERSISTENT, || "".to_string());
        if !user_info_persistent.read().is_empty() {
            *user_info_persistent.write() = "".to_string();
        }
    });
}

pub fn set_server_user_info(user: Option<Signal<UserModel>>) {
    let mut user_json_string = serde_json::to_string(&user).unwrap().to_string();
    let has_param = user.is_some();
    if !has_param {
        let user_info_persistent =
            new_persistent(KEY_OF_SERVER_USER_INFO_OF_PERSISTENT, || "".to_string());
        user_json_string = user_info_persistent.read().clone();
        if user_json_string.is_empty() {
            remove_server_user_info();
            return;
        }
    }
    spawn_forever_global_call(move || {
        let user: UserModel = serde_json::from_str(user_json_string.as_str()).unwrap();
        *SERVER_USER_INFO.write() = user;
        if has_param {
            let mut user_info_persistent =
                new_persistent(KEY_OF_SERVER_USER_INFO_OF_PERSISTENT, || "".to_string());
            *user_info_persistent.write() = user_json_string;
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
