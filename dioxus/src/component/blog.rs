use crate::router::Route;
use dioxus::prelude::*;
use lumen_blocks::components::button::Button;
use lumen_blocks::components::button::ButtonVariant;

#[component]
pub fn Blog(id: i128) -> Element {
    rsx! {

        div {
            id: "blog",
            class: "flex flex-col flex-auto m-3",
            h1 {
                "This is blog #{id}!"
            }
            p {
                "In blog #{id}, we show how the Dioxus router works and how URL parameters can be passed as props to our route components."
            }
            div {
                class: "flex flex-row mt-3",
                Link {
                    to: Route::BlogPage { id: id - 1 },
                    Button {
                        variant: ButtonVariant::Primary,
                        "Previous"
                    }
                }
                div {
                    class: "flex flex-row items-center ml-3 mr-3",
                    " <---> "
                }
                Link {
                    to: Route::BlogPage { id: id + 1 },
                    Button {
                        variant: ButtonVariant::Primary,
                        "Next"
                    }
                }
            }
        }
    }
}
