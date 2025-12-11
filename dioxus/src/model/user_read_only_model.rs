use bigdecimal::BigDecimal;
use chrono::DateTime;
use chrono::Local;
use derive_more::Display;
use serde::Deserialize;
use serde::Serialize;
use serde_json::to_string_pretty;

#[derive(Debug, Display, Clone, Default, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
#[display("{}", to_string_pretty(self).unwrap())]
pub struct UserReadOnlyModel {
    pub id: String,
    pub username: String,
    pub money: Option<BigDecimal>,
    pub create_date: DateTime<Local>,
    pub update_date: DateTime<Local>,
    pub access_token: String,
}
