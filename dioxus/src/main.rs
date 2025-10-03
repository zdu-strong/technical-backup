use crate::app::App;
pub mod app;
pub mod components;
pub mod constant;
pub mod enums;
pub mod model;
pub mod route;

fn main() {
    dioxus::launch(App);
}
