use async_trait::async_trait;
use derive_more::Display;
use serde::Deserialize;
use serde::Serialize;
use serde_aux::prelude::*;
use serde_json::to_string_pretty;
use std::fmt::Debug;
use std::fmt::Display;

#[derive(Debug, Display, Clone, Default, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
#[display("{}", to_string_pretty(self).unwrap())]
pub struct PixelModel {
    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub price: String,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub owner: String,
}

#[async_trait]
pub trait Buy: 'static + Debug + Display + Send + Sync {
    async fn buy(&mut self);
}

#[async_trait]
impl Buy for PixelModel {
    async fn buy(&mut self) {
        println!("{} cost {} buy pixel", self.owner, self.price);
    }
}
