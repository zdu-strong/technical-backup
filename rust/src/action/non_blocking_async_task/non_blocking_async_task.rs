use tokio::task::block_in_place;

pub async fn non_blocking_async_task() {
    let _ = block_in_place(move || async {
        // heavy_cpu
        return "Hello World".to_string();
    })
    .await;
}
