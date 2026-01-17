use crate::router::Route;
use daisy_rsx::*;
use dioxus::prelude::*;

#[component]
pub fn Navbar() -> Element {
    #[css_module("/assets/styling/navbar.module.css")]
    struct Styles;

    rsx! {
        div {
            id: "navbar",
            class: format!("{} {} {}", "flex", "flex-row", Styles::navbar),

            Link {
                to: Route::HomePage {},
                Button {
                    button_scheme: ButtonScheme::Secondary,
                    "Home"
                }
            }
            Link {
                to: Route::BlogPage { id: 1 },
                Button {
                    button_scheme: ButtonScheme::Secondary,
                    "Blog"
                }
            }
        }

        div {
            class: "flex flex-col flex-auto",
            Outlet::<Route> {}
        }
    }
}
