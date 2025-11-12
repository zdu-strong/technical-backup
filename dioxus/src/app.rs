use crate::router::Route;
use dioxus::prelude::*;

const GOOGLE_ROBOT_FONT_CSS: Asset = asset!(
    "/assets/google_material/google_robot_font.css",
    AssetOptions::css()
        .with_preload(true)
        .with_static_head(true)
);
const TAILWIND_CSS: Asset = asset!(
    "/assets/tailwind/tailwind.css",
    AssetOptions::css()
        .with_preload(true)
        .with_static_head(true)
);
const TAILWIND_JS: Asset = asset!(
    "/assets/tailwind/tailwind.js",
    AssetOptions::js().with_preload(true).with_static_head(true)
);
const MAIN_CSS: Asset = asset!(
    "/assets/styling/main.css",
    AssetOptions::css()
        .with_preload(true)
        .with_static_head(true)
);
const FAVICON: Asset = asset!("/assets/favicon.ico");

#[component]
pub fn App() -> Element {
    rsx! {
        document::Link { rel: "stylesheet", href: GOOGLE_ROBOT_FONT_CSS }
        document::Link { rel: "stylesheet", href: TAILWIND_CSS }
        document::Script { src: TAILWIND_JS }
        document::Link { rel: "stylesheet", href: MAIN_CSS }
        document::Link { rel: "icon", href: FAVICON }
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
