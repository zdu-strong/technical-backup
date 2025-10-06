use chrono::DateTime;
use chrono::Local;
use serde::Deserialize;
use serde::Serialize;

#[derive(Debug, Clone, Serialize, Deserialize, Default)]
#[serde(rename_all = "camelCase")]
pub struct UserModel {
    pub id: String,
    pub name: String,
    pub create_date: DateTime<Local>,
    pub update_date: DateTime<Local>,
}
