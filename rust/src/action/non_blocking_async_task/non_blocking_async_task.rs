use tokio::task::spawn_blocking;

pub async fn non_blocking_async_task() {
    let _ = spawn_blocking(move || async move {
        // heavy_cpu
        return "Hello World".to_string();
    })
    .await
    .unwrap()
    .await;
}
