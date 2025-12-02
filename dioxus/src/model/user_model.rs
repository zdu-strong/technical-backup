use bigdecimal::BigDecimal;
use chrono::DateTime;
use chrono::Local;
use dioxus::prelude::*;
use serde::Deserialize;
use serde::Serialize;

#[derive(Debug, Clone, Copy, Default, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct UserModel {
    pub id: Signal<String>,
    pub username: Signal<String>,
    pub money: Signal<Option<BigDecimal>>,
    pub create_date: Signal<DateTime<Local>>,
    pub update_date: Signal<DateTime<Local>>,
    pub access_token: Signal<String>,
}
