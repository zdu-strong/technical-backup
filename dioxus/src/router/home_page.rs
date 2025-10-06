use crate::component::hero::Hero;
use dioxus::prelude::*;

#[component]
pub fn HomePage() -> Element {
    rsx! {
        Hero {}
    }
}
