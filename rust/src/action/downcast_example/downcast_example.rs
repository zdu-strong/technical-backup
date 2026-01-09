use crate::model::pixel_model::PixelModel;
use crate::traits::buy::Buy;
use std::any::Any;

pub async fn downcast_any_example() {
    let phone: Box<dyn Buy> = Box::new(PixelModel {
        price: "10,000".to_string(),
        owner: "Jerry".to_string(),
    });
    let mut pixel = *(phone as Box<dyn Any>).downcast::<PixelModel>().unwrap();
    pixel.buy().await;
}
