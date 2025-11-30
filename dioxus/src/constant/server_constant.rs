use dioxus::signals::GlobalSignal;
use dioxus::signals::ReadableExt;
use reqwest::Client;
use reqwest::RequestBuilder;
use reqwest::Url;

pub const SERVER_ADDRESS: GlobalSignal<String> =
    GlobalSignal::new(|| "http://localhost:8080".to_string());

pub const SERVER_ACCESS_TOKEN: GlobalSignal<String> = GlobalSignal::new(|| "".to_string());

pub fn get(url: &str) -> RequestBuilder {
    get_request_builder(Client::new().get(get_server_url(url)))
}

pub fn post(url: &str) -> RequestBuilder {
    get_request_builder(Client::new().post(get_server_url(url)))
}

pub fn put(url: &str) -> RequestBuilder {
    get_request_builder(Client::new().put(get_server_url(url)))
}

pub fn delete(url: &str) -> RequestBuilder {
    get_request_builder(Client::new().delete(get_server_url(url)))
}

fn get_request_builder(request_builder: RequestBuilder) -> RequestBuilder {
    if !SERVER_ACCESS_TOKEN.read().is_empty() {
        return request_builder.bearer_auth(SERVER_ACCESS_TOKEN.read().as_str());
    } else {
        return request_builder;
    }
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
