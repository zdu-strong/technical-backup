use crate::app::App;
pub mod api;
pub mod app;
pub mod component;
pub mod constant;
pub mod enums;
pub mod model;
pub mod router;

fn main() {
    dioxus::launch(App);
}
