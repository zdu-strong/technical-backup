use bigdecimal::BigDecimal;
use chrono::DateTime;
use chrono::Local;
use derive_more::Display;
use dioxus::prelude::*;
use serde::Deserialize;
use serde::Serialize;
use serde_json::to_string_pretty;

#[derive(Debug, Display, Clone, Default, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
#[display("{}", to_string_pretty(self).unwrap())]
pub struct UserModel {
    pub id: Signal<String>,
    pub username: Signal<String>,
    pub money: Signal<Option<BigDecimal>>,
    pub create_date: Signal<DateTime<Local>>,
    pub update_date: Signal<DateTime<Local>>,
    pub access_token: Signal<String>,
}
