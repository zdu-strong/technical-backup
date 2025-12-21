use crate::constant::server_constant::*;
use crate::model::user_model::UserModel;
use dioxus::prelude::*;
use serde_json::json;

pub async fn sign_in(username: Signal<String>, password: Signal<String>) {
    let user = post("/sign-in")
        .query(&json!({
            "username": username,
            "password": password,
        }))
        .send()
        .await
        .unwrap()
        .json::<Signal<Option<UserModel>>>()
        .await
        .unwrap();
    set_server_user_info(user);
}

pub async fn sign_out() {
    if !SERVER_USER_INFO().access_token.is_empty() {
        post("/sign-out").send().await.unwrap();
        remove_server_user_info();
    }
}
