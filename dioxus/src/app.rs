use crate::router::Route;
use dioxus::prelude::*;
use dioxus_i18n::prelude::*;
use dioxus_i18n::unic_langid::langid;

const _: Asset = asset!(
    "/assets/common/google-material/google-robot-font.css",
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
const STYLANCE_CSS: &str = include_str!("../assets/stylance.bundled.css");
const EN_US_JSON: &str = include_str!("../assets/i18n/en-US.ftl");

#[component]
pub fn App() -> Element {
    use_init_i18n(|| {
        I18nConfig::new(langid!("en-US"))
            // implicit [`Locale`]
            .with_locale((
                // Embed
                langid!("en-US"),
                EN_US_JSON,
            ))
    });

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
