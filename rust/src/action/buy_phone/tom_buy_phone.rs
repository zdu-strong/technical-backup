use crate::model::pixel_model::PixelModel;
use crate::traits::buy::Buy;

pub async fn tom_buy_phone() {
    let ref mut phone_list = vec![
        PixelModel {
            price: "10,000".to_string(),
            owner: "Tom".to_string(),
        },
        PixelModel {
            price: "10,000".to_string(),
            owner: "Tom".to_string(),
        },
    ];
    println!("phone_service {:?}", phone_list);
    for phone in phone_list {
        phone.buy().await;
    }
}
