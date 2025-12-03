use crate::traits::phone::iphone_trait::IPhoneTrait;
use crate::traits::phone::phone_trait::PhoneTrait;
use crate::traits::phone::pixel_trait::PixelTrait;
use futures::stream::iter;
use futures::prelude::*;

pub async fn jerry_buy_phone() {
    let ref mut phone_list = vec![
        Box::new(IPhoneTrait {
            price: "10,000".to_string(),
            owner: "Jerry".to_string(),
        }) as Box<dyn PhoneTrait>,
        Box::new(PixelTrait {
            price: "3,000".to_string(),
            owner: "Jerry".to_string(),
        }),
    ];
    let _ = iter(phone_list)
        .map(|phone| async {
            phone.buy().await;
        })
        .buffer_unordered(10)
        .collect::<Vec<_>>()
        .await;
}
