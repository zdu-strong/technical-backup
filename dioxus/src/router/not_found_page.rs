use crate::component::hero::Hero;
use dioxus::prelude::*;

#[component]
pub fn NotFoundPage(route: ReadSignal<Vec<String>>) -> Element {
    rsx! {
        Hero {}
    }
}
