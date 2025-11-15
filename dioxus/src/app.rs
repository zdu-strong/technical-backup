use crate::router::Route;
use dioxus::prelude::*;

const _: Asset = asset!(
    "/assets/common/google-material/google-robot-font.css",
    AssetOptions::css().with_static_head(true)
);
const _: Asset = asset!(
    "/assets/common/app/app.css",
    AssetOptions::css().with_static_head(true)
);
const FAVICON: Asset = asset!("/assets/image/favicon.ico");
const TAILWIND_CSS: &str = include_str!("../assets/tailwind.css");
const STYLANCE_CSS: &str = include_str!("../assets/common/stylance/stylance.bundled.css");

#[component]
pub fn App() -> Element {
    rsx! {
        document::Link { rel: "icon", href: FAVICON }
        style { "{TAILWIND_CSS}" }
        style { "{STYLANCE_CSS}" }
        div {
            class: "w-screen h-screen overflow-auto",
            div {
                class: "flex flex-row min-w-full min-h-full w-max",
                div {
                    class: "flex flex-col flex-auto",
                    Router::<Route> {}
                }
            }
        }
    }
}
