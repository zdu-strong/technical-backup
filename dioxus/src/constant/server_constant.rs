use crate::model::user_model::UserModel;
use chrono::Local;
use dioxus::prelude::*;
use reqwest::Client;
use reqwest::Method;
use reqwest::RequestBuilder;
use reqwest::Url;
use uuid::Uuid;

pub const SERVER_ADDRESS: GlobalSignal<String> =
    GlobalSignal::new(|| "http://localhost:8080".to_string());

pub const SERVER_USER_INFO: GlobalSignal<UserModel> = GlobalSignal::new(|| UserModel::default());

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
    let server_url = get_server_url(url);
    let mut request_builder = Client::new().request(method.clone(), server_url.clone());
    if server_url.origin().ascii_serialization()
        == Url::parse(SERVER_ADDRESS.read().as_str())
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

fn get_server_url(url: &str) -> Url {
    Url::options()
        .base_url(Option::Some(&Url::parse(&SERVER_ADDRESS.read()).unwrap()))
        .parse(url)
        .unwrap()
}
