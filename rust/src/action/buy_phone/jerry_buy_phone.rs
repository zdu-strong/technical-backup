use futures::stream::iter;
use futures::prelude::*;
use crate::model::pixel_model::PixelModel;
use tokio::task::spawn_blocking;
use tokio::runtime::Handle;
use std::sync::Arc;
use tokio::sync::RwLock;

pub async fn jerry_buy_phone() {
    let phone_list = vec![
        Arc::new(RwLock::new(PixelModel {
            price: "10,000".to_string(),
            owner: "Jerry".to_string(),
        })),
        Arc::new(RwLock::new(PixelModel {
            price: "3,000".to_string(),
            owner: "Jerry".to_string(),
        })),
    ];
    let _ = iter(phone_list)
        .map(move |phone| async move {
            phone.write().await.buy().await;
        })
        .map(|s| async {
            spawn_blocking(|| Handle::current().block_on(s))
                .await
                .unwrap();
        })
        .buffer_unordered(10)
        .collect::<Vec<_>>()
        .await;
}
