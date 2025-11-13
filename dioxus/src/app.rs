use crate::router::Route;
use dioxus::prelude::*;

const _: Asset = asset!(
    "/assets/google_material/google_robot_font.css",
    AssetOptions::css().with_static_head(true)
);
const _: Asset = asset!(
    "/assets/tailwind/tailwind.css",
    AssetOptions::css().with_static_head(true)
);
const _: Asset = asset!(
    "/assets/styling/app.css",
    AssetOptions::css().with_static_head(true)
);
const _: Asset = asset!(
    "/assets/tailwind/tailwind.js",
    AssetOptions::js().with_static_head(true)
);
const FAVICON: Asset = asset!("/assets/favicon.ico");

#[component]
pub fn App() -> Element {
    let STYLANCE_CSS = include_str!("../assets/stylance/bundled.css");

    rsx! {
        document::Link { rel: "icon", href: FAVICON }
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
