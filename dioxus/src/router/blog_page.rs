use crate::component::blog::Blog;
use dioxus::prelude::*;

#[component]
pub fn BlogPage(id: ReadSignal<i128>) -> Element {
    rsx! {
        Blog { id }
    }
}
