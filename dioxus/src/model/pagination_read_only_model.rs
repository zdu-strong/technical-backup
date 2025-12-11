use bigdecimal::ToPrimitive;
use derive_more::Display;
use serde::Deserialize;
use serde::Serialize;
use serde_json::to_string_pretty;

#[derive(Debug, Display, Clone, Default, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
#[display("{}", to_string_pretty(self).unwrap())]
pub struct PaginationReadOnlyModel<T>
where
    T: 'static + Serialize,
{
    pub page_num: i128,
    pub page_size: i128,
    pub total_records: i128,
    pub total_pages: i128,
    pub items: Vec<T>,
}

impl<T> PaginationReadOnlyModel<T>
where
    T: 'static + Serialize,
{
    pub fn new(page_num: i128, page_size: i128, items: Vec<T>) -> PaginationReadOnlyModel<T> {
        if page_num < 1 {
            panic!("page_num cannot be less than 1")
        }
        if page_size < 1 {
            panic!("page_size cannot be less than 1")
        }
        let total_records = items.len().to_i128().unwrap();
        let total_pages =
            (total_records / page_size) + (if total_records % page_size > 0 { 1 } else { 0 });
        PaginationReadOnlyModel {
            page_num: page_num,
            page_size: page_size,
            total_records: total_records,
            total_pages: total_pages,
            items: items
                .into_iter()
                .skip(((page_num - 1) * page_size).to_usize().unwrap())
                .take(page_size.to_usize().unwrap())
                .collect::<Vec<_>>(),
        }
    }
}
