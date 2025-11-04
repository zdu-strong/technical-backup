use crate::router::Route;
use dioxus::prelude::*;
use dioxus_material::Theme;
use dioxus_i18n::unic_langid::langid;
use dioxus_i18n::prelude::*;

const FAVICON: Asset = asset!("/assets/favicon.ico");
const MAIN_CSS: Asset = asset!("/assets/styling/main.css");
const TAILWIND_JS: Asset = asset!("/assets/tailwind/tailwind.js");
const GOOGLE_ROBOT_FONT_CSS: Asset = asset!("/assets/google_material/google_robot_font.css");
const GOOGLE_MATERIAL_CSS: Asset = asset!("/assets/google_material/google_material.css");
const EN_US_JSON: &str = include_str!("../assets/i18n/en-US.ftl");

#[component]
pub fn App() -> Element {
    use_init_i18n(|| I18nConfig::new(langid!("en-US")).with_locale((langid!("en-US"), EN_US_JSON)));

    rsx! {
        document::Link { rel: "icon", href: FAVICON }
        document::Link { rel: "stylesheet", href: MAIN_CSS }
        document::Link { rel: "stylesheet", href: GOOGLE_ROBOT_FONT_CSS }
        document::Link { rel: "stylesheet", href: GOOGLE_MATERIAL_CSS }
        document::Script { src: TAILWIND_JS }
        Theme { Router::<Route> {} }
    }
}
