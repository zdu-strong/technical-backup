use crate::constant::server_constant::*;
use crate::model::user_model::UserModel;
use serde_json::json;

pub async fn sign_in(username: &mut String, password: &mut String) {
    let ref mut user = post("/sign-in")
        .await
        .query(&json!({
            "username": username,
            "password": password,
        }))
        .send()
        .await
        .unwrap()
        .json::<Option<UserModel>>()
        .await
        .unwrap();
    set_server_user_info(user);
}

pub async fn sign_out() {
    if !SERVER_USER_INFO.read().await.access_token.is_empty() {
        let _ = post("/sign-out").await.send().await;
        remove_server_user_info();
    }
}
