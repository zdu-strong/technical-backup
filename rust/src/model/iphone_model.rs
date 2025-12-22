use derive_more::Display;
use serde::Deserialize;
use serde::Serialize;
use serde_aux::prelude::*;
use serde_json::to_string_pretty;

#[derive(Debug, Display, Clone, Default, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
#[display("{}", to_string_pretty(self).unwrap())]
pub struct IPhoneModel {
    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub price: String,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub owner: String,
}

impl IPhoneModel {
    pub async fn buy(&mut self) {
        println!("{} cost {} buy iphone", self.owner, self.price);
    }
}
