use derive_more::Display;
use serde::Deserialize;
use serde::Serialize;
use serde_json::to_string_pretty;
use strum_macros::AsRefStr;
use strum_macros::EnumIter;
use strum_macros::EnumString;

#[derive(Debug, Display, Clone, Serialize, Deserialize, EnumIter, EnumString, AsRefStr)]
#[display("{}", to_string_pretty(self).unwrap())]
pub enum AnimalEnum {
    #[serde(rename = "TIGER")]
    #[strum(serialize = "TIGER")]
    TIGER,

    #[serde(rename = "DOG")]
    #[strum(serialize = "DOG")]
    DOG,
}
