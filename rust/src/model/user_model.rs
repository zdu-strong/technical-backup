use chrono::DateTime;
use chrono::Local;
use serde::Deserialize;
use serde::Serialize;
use derive_more::Display;
use serde_json::to_string_pretty;

#[derive(Debug, Display, Clone, Default, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
#[display("{}", to_string_pretty(self).unwrap())]
pub struct UserModel {
    pub id: String,
    pub name: String,
    pub create_date: DateTime<Local>,
    pub update_date: DateTime<Local>,
}
