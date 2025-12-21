use derive_more::Display;
use dioxus::prelude::*;
use serde::Deserialize;
use serde::Serialize;
use serde_aux::prelude::*;
use serde_json::to_string_pretty;

#[derive(Debug, Display, Clone, Default, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
#[display("{}", to_string_pretty(self).unwrap())]
pub struct CatModel {
    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub id: Signal<String>,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub name: Signal<String>,
}
