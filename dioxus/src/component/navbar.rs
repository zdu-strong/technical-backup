use crate::router::Route;
use daisy_rsx::*;
use dioxus::prelude::*;
use stylance::import_crate_style;

import_crate_style!(styles, "assets/styling/navbar.module.css");

#[component]
pub fn Navbar() -> Element {
    rsx! {
        div {
            id: "navbar",
            class: format!("{} {} {}", "flex", "flex-row", styles::navbar),

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
