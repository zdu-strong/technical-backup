use crate::model::pixel_model::Buy;
use crate::model::pixel_model::PixelModel;
use futures::prelude::*;
use futures::stream::iter;
use tokio::runtime::Handle;
use tokio::task::spawn_blocking;

pub async fn jerry_buy_phone() {
    let phone_list = vec![
        Box::new(PixelModel {
            price: "10,000".to_string(),
            owner: "Jerry".to_string(),
        }) as Box<dyn Buy>,
        Box::new(PixelModel {
            price: "3,000".to_string(),
            owner: "Jerry".to_string(),
        }),
    ];
    let _ = iter(phone_list)
        .map(move |mut phone| async move {
            phone.buy().await;
        })
        .map(|s| async {
            spawn_blocking(|| Handle::current().block_on(s))
                .await
                .unwrap()
        })
        .buffer_unordered(10)
        .collect::<Vec<_>>()
        .await;
}
