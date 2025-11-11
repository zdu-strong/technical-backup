use crate::router::Route;
use dioxus::prelude::*;
use lumen_blocks::components::button::Button;
use lumen_blocks::components::button::ButtonVariant;
use tailwind_rs_dioxus::*;

const NAVBAR_CSS: Asset = asset!("/assets/styling/navbar.css");

#[component]
pub fn Navbar() -> Element {
    let navbar_css = ClassBuilder::new()
        .margin(SpacingValue::Fractional(3.0))
        .class("flex")
        .class("flex-row")
        .build_string();

    rsx! {
        document::Link { rel: "stylesheet", href: NAVBAR_CSS }

        div {
            id: "navbar",
            class: navbar_css,

            Link {
                to: Route::HomePage {},
                Button {
                    variant: ButtonVariant::Primary,
                    "Home"
                }
            }
            Link {
                to: Route::BlogPage { id: 1 },
                Button {
                    variant: ButtonVariant::Primary,
                    "Blog"
                }
            }
        }

        div {
            class: "flex flex-col flex-full",
            Outlet::<Route> {}
        }
    }
}
