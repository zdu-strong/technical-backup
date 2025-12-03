use serde::Deserialize;
use serde::Serialize;
use derive_more::Display;
use serde_json::to_string_pretty;

#[derive(Debug, Display, Clone, Default, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
#[display("{}", to_string_pretty(self).unwrap())]
pub struct PixelModel {
    pub price: String,
    pub owner: String,
}

impl PixelModel {
    pub async fn buy(&mut self) {
        println!("{} cost {} buy pixel", self.owner, self.price);
    }
}
