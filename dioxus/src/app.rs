use crate::common::i18n::use_app_i18n::use_app_i18n;
use crate::router::Route;
use dioxus::prelude::*;

const _: Asset = asset!(
    "/assets/common/jetbrains-mono-font/jetbrains-mono.css",
    AssetOptions::css().with_static_head(true)
);
const _: Asset = asset!(
    "/assets/common/app/app.css",
    AssetOptions::css().with_static_head(true)
);
const _: Asset = asset!(
    "/assets/common/daisyui/v4.12.24/daisyui.css",
    AssetOptions::css().with_static_head(true)
);
const FAVICON: Asset = asset!("/assets/image/favicon.ico");
const TAILWIND_CSS: &str = include_str!("../assets/tailwind.css");

#[component]
pub fn App() -> Element {
    use_app_i18n();

    rsx! {
        document::Link { rel: "icon", href: FAVICON }
        style { "{TAILWIND_CSS}" }
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
