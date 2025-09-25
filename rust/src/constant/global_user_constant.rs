use std::sync::Arc;
use lazy_static::lazy_static;
use tokio::sync::RwLock;

lazy_static! {
    pub static ref GLOBAL_USERNAME: Arc<RwLock<String>> = Arc::new(RwLock::new("tom".to_string()));
}
