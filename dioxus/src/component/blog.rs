use crate::router::Route;
use dioxus::prelude::*;

const BLOG_CSS: Asset = asset!("/assets/styling/blog.css");

#[component]
pub fn Blog(id: i128) -> Element {
    rsx! {
        document::Link { rel: "stylesheet", href: BLOG_CSS }

        div {
            id: "blog",
            h1 {
                "This is blog #{id}!"
            }
            p {
                "In blog #{id}, we show how the Dioxus router works and how URL parameters can be passed as props to our route components."
            }
            Link {
                to: Route::BlogPage { id: id - 1 },
                "Previous"
            }
            span {
                " <---> "
            }
            Link {
                to: Route::BlogPage { id: id + 1 },
                "Next"
            }
        }
    }
}
