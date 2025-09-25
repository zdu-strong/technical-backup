use crate::constant::global_user_constant::GLOBAL_USERNAME;

pub async fn print_username() {
    *GLOBAL_USERNAME.write().await = "Jerry".to_string();
    let username = GLOBAL_USERNAME.read().await;
    println!("My name is {:?}", username);
}
