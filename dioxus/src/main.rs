use crate::app::App;
pub mod app;
pub mod component;
pub mod constant;
pub mod enums;
pub mod model;
pub mod router;

#[tokio_with_wasm::alias::main(flavor = "current_thread")]
async fn main() {
    dioxus::launch(App);
}
