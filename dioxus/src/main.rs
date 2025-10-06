use crate::app::App;
pub mod app;
pub mod component;
pub mod constant;
pub mod enums;
pub mod model;
pub mod route;

fn main() {
    dioxus::launch(App);
}
