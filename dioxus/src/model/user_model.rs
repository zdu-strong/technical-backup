use chrono::DateTime;
use chrono::Local;
use dioxus::signals::Signal;
use serde::Deserialize;
use serde::Serialize;

#[derive(Debug, Clone, Copy, Default, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct UserModel {
    pub id: Signal<String>,
    pub name: Signal<String>,
    pub create_date: Signal<Option<DateTime<Local>>>,
    pub update_date: Signal<Option<DateTime<Local>>>,
}
