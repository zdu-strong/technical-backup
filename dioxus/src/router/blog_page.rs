use crate::component::blog::Blog;
use dioxus::prelude::*;

#[component]
pub fn BlogPage(id: i128) -> Element {
    rsx! {
        Blog { id }
    }
}
