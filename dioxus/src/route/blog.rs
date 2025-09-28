use crate::route::Route;
use dioxus::prelude::*;

const BLOG_CSS: Asset = asset!("/assets/styling/blog.css");

#[derive(Debug, Clone, Copy, Default, Props, PartialEq)]
pub struct Props {
    pub id: i32,
}

#[component]
pub fn Blog(props: Props) -> Element {
    rsx! {
        document::Link { rel: "stylesheet", href: BLOG_CSS }

        div { id: "blog",

            h1 { "This is blog #{props.id}!" }
            p {
                "In blog #{props.id}, we show how the Dioxus router works and how URL parameters can be passed as props to our route components."
            }

            Link { to: Route::Blog { id: props.id - 1 }, "Previous" }
            span { " <---> " }
            Link { to: Route::Blog { id: props.id + 1 }, "Next" }
        }
    }
}
