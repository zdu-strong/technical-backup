use dioxus::prelude::*;
use rust_decimal::prelude::*;
use serde::Deserialize;
use serde::Serialize;

#[derive(Debug, Clone, Copy, Default, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct PaginationModel<T>
where
    T: 'static,
{
    pub page_num: Signal<i128>,
    pub page_size: Signal<i128>,
    pub total_records: Signal<i128>,
    pub total_pages: Signal<i128>,
    pub items: Signal<Vec<Signal<T>>>,
}

impl<T> PaginationModel<T> {
    pub fn from(page_num: i128, page_size: i128, items: Vec<T>) -> PaginationModel<T> {
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
                    .map(|item| Signal::new(item))
                    .collect::<Vec<Signal<_>>>(),
            ),
        }
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
