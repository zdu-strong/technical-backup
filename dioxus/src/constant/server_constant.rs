use crate::model::user_model::UserModel;
use chrono::Local;
use dioxus::prelude::*;
use futures::Future;
use reqwest::Client;
use reqwest::Method;
use reqwest::RequestBuilder;
use reqwest::Url;
use uuid::Uuid;

pub const SERVER_ADDRESS: GlobalSignal<String> =
    GlobalSignal::new(|| "http://localhost:8080".to_string());

pub const SERVER_USER_INFO: GlobalSignal<UserModel> = GlobalSignal::new(|| UserModel::default());

pub fn remove_server_user_info() {
    spawn_forever_global_call(async move {
        *SERVER_USER_INFO.write() = UserModel::default();
    });
}

pub fn set_server_user_info(user: Signal<UserModel>) {
    let user_json_string = serde_json::to_string(&user).unwrap().to_string();
    spawn_forever_global_call(async move {
        let user: UserModel = serde_json::from_str(user_json_string.as_str()).unwrap();
        *SERVER_USER_INFO.write() = user;
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
    let server_url = get_server_url(url, server_address.as_str());
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

fn get_server_url(url: &str, server_address: &str) -> Url {
    Url::options()
        .base_url(Option::Some(&Url::parse(server_address).unwrap()))
        .parse(url)
        .unwrap()
}

fn spawn_forever_global_call(fut: impl Future<Output = ()> + 'static) {
    let task = dioxus::core::spawn_forever(fut).poll_now();
    while !task.is_ready() {}
}
