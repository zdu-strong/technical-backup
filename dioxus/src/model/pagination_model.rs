use bigdecimal::ToPrimitive;
use derive_more::Display;
use dioxus::signals::Signal;
use serde::de::DeserializeOwned;
use serde::Deserialize;
use serde::Serialize;
use serde_aux::prelude::*;
use serde_json::to_string_pretty;
use std::fmt::Debug;
use std::fmt::Display;

#[derive(Debug, Display, Clone, Default, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
#[display("{}", to_string_pretty(self).unwrap())]
#[serde(bound = "T: Serialize + DeserializeOwned")]
pub struct PaginationModel<T>
where
    T: 'static + Debug + Display + Clone + Default + Serialize + DeserializeOwned,
{
    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub page_num: Signal<i128>,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub page_size: Signal<i128>,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub total_records: Signal<i128>,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub total_pages: Signal<i128>,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub items: Signal<Vec<Signal<T>>>,
}

impl<T> PaginationModel<T>
where
    T: 'static + Debug + Display + Clone + Default + Serialize + DeserializeOwned,
{
    pub fn from(page_num: i128, page_size: i128, items: Vec<T>) -> PaginationModel<T> {
        Self::new(
            page_num,
            page_size,
            items
                .into_iter()
                .map(|item| Signal::new(item))
                .collect::<Vec<Signal<_>>>(),
        )
    }

    pub fn new(page_num: i128, page_size: i128, items: Vec<Signal<T>>) -> PaginationModel<T> {
        if page_num < 1 {
            panic!("page_num cannot be less than 1")
        }
        if page_size < 1 {
            panic!("page_size cannot be less than 1")
        }
        let total_records = items.len().to_i128().unwrap();
        let total_pages =
            (total_records / page_size) + (if total_records % page_size > 0 { 1 } else { 0 });
        PaginationModel {
            page_num: Signal::new(page_num),
            page_size: Signal::new(page_size),
            total_records: Signal::new(total_records),
            total_pages: Signal::new(total_pages),
            items: Signal::new(
                items
                    .into_iter()
                    .skip(((page_num - 1) * page_size).to_usize().unwrap())
                    .take(page_size.to_usize().unwrap())
                    .collect::<Vec<Signal<_>>>(),
            ),
        }
    }
}
