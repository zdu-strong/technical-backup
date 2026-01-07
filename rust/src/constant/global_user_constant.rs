use once_cell::sync::Lazy;
use std::sync::Arc;
use tokio::sync::RwLock;

pub static GLOBAL_USERNAME: Lazy<Arc<RwLock<String>>> =
    Lazy::new(|| Arc::new(RwLock::new("tom".to_string())));
