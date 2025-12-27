use chrono::DateTime;
use chrono::Local;
use derive_more::Display;
use dioxus::prelude::*;
use serde::Deserialize;
use serde::Serialize;
use serde_aux::prelude::*;
use serde_json::to_string_pretty;

#[derive(Debug, Display, Clone, Default, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
#[display("{}", to_string_pretty(self).unwrap())]
pub struct OrganizeModel {
    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub id: Signal<String>,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub name: Signal<String>,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub level: Signal<i128>,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub child_count: Signal<i128>,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub descendant_count: Signal<i128>,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub create_date: Signal<DateTime<Local>>,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub update_date: Signal<DateTime<Local>>,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub parent: Signal<Option<OrganizeModel>>,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub child_list: Signal<Vec<Signal<OrganizeModel>>>,
}
