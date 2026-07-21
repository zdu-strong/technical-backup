use crate::action::eat_food::eat_apple::eat_apple;
use std::sync::Arc;
use tokio::sync::RwLock;

pub async fn tom_eat_apple() {
    let username = "tom".to_string();
    let max_apple_count = 10;
    let surplus_apple_count = Arc::new(RwLock::new(max_apple_count));
    eat_apple(username, surplus_apple_count, max_apple_count).await;
}
