use chrono::DateTime;
use chrono::Local;
use dioxus::prelude::*;
use rust_decimal::prelude::*;
use serde::Deserialize;
use serde::Serialize;

#[derive(Debug, Clone, Copy, Default, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct UserModel {
    pub id: Signal<String>,
    pub name: Signal<String>,
    pub money: Signal<Decimal>,
    pub create_date: Signal<DateTime<Local>>,
    pub update_date: Signal<DateTime<Local>>,
}
