use crate::app::App;
pub mod app;
pub mod components;
pub mod enums;
pub mod model;
pub mod route;

#[tokio_with_wasm::alias::main(flavor = "current_thread")]
async fn main() {
    dioxus::launch(App);
}
