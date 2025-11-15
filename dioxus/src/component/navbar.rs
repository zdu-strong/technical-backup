use crate::router::Route;
use dioxus::prelude::*;
use lumen_blocks::components::button::Button;
use lumen_blocks::components::button::ButtonVariant;
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
            class: "flex flex-col flex-auto",
            Outlet::<Route> {}
        }
    }
}
