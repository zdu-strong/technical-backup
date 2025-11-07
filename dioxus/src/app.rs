use crate::router::Route;
use dioxus::prelude::*;

const FAVICON: Asset = asset!("/assets/favicon.ico");
const MAIN_CSS: Asset = asset!("/assets/styling/main.css");
const TAILWIND_JS: Asset = asset!("/assets/tailwind/tailwind.js");
const GOOGLE_ROBOT_FONT_CSS: Asset = asset!("/assets/google_material/google_robot_font.css");
const GOOGLE_MATERIAL_CSS: Asset = asset!("/assets/google_material/google_material.css");

#[component]
pub fn App() -> Element {
    rsx! {
        document::Link { rel: "icon", href: FAVICON }
        document::Link { rel: "stylesheet", href: MAIN_CSS }
        document::Link { rel: "stylesheet", href: GOOGLE_ROBOT_FONT_CSS }
        document::Link { rel: "stylesheet", href: GOOGLE_MATERIAL_CSS }
        document::Script { src: TAILWIND_JS }
        Router::<Route> {}
    }
}
