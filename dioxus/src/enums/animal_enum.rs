use derive_more::Display;
use strum_macros::AsRefStr;
use strum_macros::EnumIter;
use strum_macros::EnumString;

#[derive(Debug, Display, Clone, EnumIter, EnumString, AsRefStr)]
#[display("{}", self.as_ref())]
pub enum AnimalEnum {
    #[strum(serialize = "TIGER")]
    TIGER,

    #[strum(serialize = "DOG")]
    DOG,
}
