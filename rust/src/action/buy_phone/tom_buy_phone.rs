use crate::model::pixel_model::PixelModel;
use crate::traits::buy::Buy;
use bigdecimal::BigDecimal;
use std::str::FromStr;

pub async fn tom_buy_phone() {
    let ref mut phone_list = vec![
        PixelModel {
            price: BigDecimal::from_str("10000").unwrap(),
            owner: "Tom".to_string(),
        },
        PixelModel {
            price: BigDecimal::from_str("10000").unwrap(),
            owner: "Tom".to_string(),
        },
    ];
    println!("phone_service {:?}", phone_list);
    for phone in phone_list {
        phone.buy().await;
    }
}
