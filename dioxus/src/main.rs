use crate::app::App;
pub mod components;
pub mod route;
pub mod app;

fn main() {
    dioxus::launch(App);
}
