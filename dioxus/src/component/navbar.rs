use crate::router::Route;
use dioxus::prelude::*;
use lumen_blocks::components::button::Button;
use lumen_blocks::components::button::ButtonVariant;
use tailwind_rs_dioxus::*;
use styled::style;

#[component]
pub fn Navbar() -> Element {
    let navbar_css = ClassBuilder::new()
        .margin(SpacingValue::Fractional(3.0))
        .class("flex")
        .class("flex-row")
        .class(
            style!(
                & {
                }
                & a {
                    margin-right: 20px;
                    text-decoration: none;
                    transition: color 0.2s ease;
                }
                & a:hover {
                    cursor: pointer;
                    color: green;
                }
            )
            .unwrap()
            .get_class_name(),
        )
        .build_string();

    rsx! {

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
