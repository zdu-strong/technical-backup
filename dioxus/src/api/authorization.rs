use crate::model::user_model::UserModel;
use dioxus::prelude::*;
use reqwest::Client;
use serde_json::json;

pub async fn sign_in(username: Signal<String>, password: Signal<String>) -> UserModel {
    Client::new()
        .post("http://localhost:8080/sign-in")
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
