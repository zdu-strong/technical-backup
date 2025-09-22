use crate::app::App;
pub mod app;
pub mod components;
pub mod route;

fn main() {
    dioxus::launch(App);
}
