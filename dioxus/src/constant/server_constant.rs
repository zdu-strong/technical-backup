use chrono::Local;
use dioxus::signals::GlobalSignal;
use dioxus::signals::ReadableExt;
use reqwest::Client;
use reqwest::Method;
use reqwest::RequestBuilder;
use reqwest::Url;
use uuid::Uuid;

pub const SERVER_ADDRESS: GlobalSignal<String> =
    GlobalSignal::new(|| "http://localhost:8080".to_string());

pub const SERVER_ACCESS_TOKEN: GlobalSignal<String> = GlobalSignal::new(|| "".to_string());

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
    let mut request_builder = Client::new().request(method.clone(), server_url);
    if !SERVER_ACCESS_TOKEN.read().is_empty() {
        request_builder = request_builder.bearer_auth(SERVER_ACCESS_TOKEN.read().as_str());
    }
    if Method::GET != method {
        request_builder = request_builder.header("X-nonce", Uuid::new_v4().to_string());
        request_builder = request_builder.header(
            "X-Timestamp",
            serde_json::to_string(&Local::now())
                .unwrap()
                .replace("\"", ""),
        );
    }
    return request_builder;
}

fn get_server_url(url: &str) -> Url {
    let mut server_url = Url::parse(&SERVER_ADDRESS.read()).unwrap();
    let server_path_list_url = Url::options()
        .base_url(Option::Some(&server_url))
        .parse(url)
        .unwrap();
    let server_path_list_option = server_path_list_url.path_segments();
    if server_path_list_option.is_some() {
        let server_path_list = server_path_list_option.unwrap();
        for server_path in server_path_list {
            server_url.path_segments_mut().unwrap().push(server_path);
        }
    }
    server_url
}
