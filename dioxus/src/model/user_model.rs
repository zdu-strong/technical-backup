use crate::model::user_email_model::UserEmailModel;
use bigdecimal::BigDecimal;
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
pub struct UserModel {
    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub id: Signal<String>,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub username: Signal<String>,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub money: Signal<Option<BigDecimal>>,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub create_date: Signal<DateTime<Local>>,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub update_date: Signal<DateTime<Local>>,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub access_token: Signal<String>,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub passowrd: Signal<String>,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub user_email_list: Signal<Vec<Signal<UserEmailModel>>>,
}
