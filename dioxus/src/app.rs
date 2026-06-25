use crate::common::i18n::use_app_i18n::use_app_i18n;
use crate::constant::app_assets_constant::FAVICON;
use crate::constant::app_assets_constant::TAILWIND_CSS;
use crate::router::Route;
use dioxus::prelude::*;

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
