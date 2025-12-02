use derive_more::Display;
use dioxus::prelude::*;
use serde::Deserialize;
use serde::Serialize;

#[derive(Debug, Display, Clone, Copy, Default, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
#[display("{}", serde_json::to_string_pretty(self).unwrap())]
pub struct CatModel {
    pub id: Signal<String>,
    pub name: Signal<String>,
}
