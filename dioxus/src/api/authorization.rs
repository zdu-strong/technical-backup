use crate::constant::server_constant::post;
use crate::model::user_model::UserModel;
use dioxus::prelude::*;
use serde_json::json;

pub async fn sign_in(username: Signal<String>, password: Signal<String>) -> UserModel {
    post("/sign-in")
        .query(&json!({
            "username": username,
            "password": password,
        }))
        .send()
        .await
        .unwrap()
        .json::<UserModel>()
        .await
        .unwrap()
}
