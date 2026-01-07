use tokio::runtime::Handle;
use tokio::task::spawn_blocking;

pub async fn spawn_blocking_async_task() {
    let _ = spawn_blocking(|| {
        Handle::current().block_on(async move {
            // heavy_cpu
            return "Hello World".to_string();
        })
    })
    .await
    .unwrap();
}
