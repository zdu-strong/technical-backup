use crate::router::Route;
use dioxus::html::u::page_break_after;
use dioxus::prelude::*;
use lumen_blocks::components::button::Button;
use lumen_blocks::components::button::ButtonVariant;
use tailwind_rs_dioxus::*;

#[component]
pub fn Blog(id: i128) -> Element {
    let blog_css = ClassBuilder::new()
        .margin(SpacingValue::Fractional(3.0))
        .class("flex")
        .class("flex-col")
        .class("flex-auto")
        .build_string();

    let page_css = ClassBuilder::new()
        .margin_left(SpacingValue::Fractional(3.0))
        .margin_right(SpacingValue::Fractional(3.0))
        .class("flex")
        .class("flex-row")
        .class("items-center")
        .build_string();
    let container_css = ClassBuilder::new()
        .margin_top(SpacingValue::Fractional(3.0))
        .class("flex")
        .class("flex-row")
        .build_string();

    rsx! {

        div {
            id: "blog",
            class: blog_css,
            h1 {
                "This is blog #{id}!"
            }
            p {
                "In blog #{id}, we show how the Dioxus router works and how URL parameters can be passed as props to our route components."
            }
            div {
                class: container_css,
                Link {
                    to: Route::BlogPage { id: id - 1 },
                    Button {
                        variant: ButtonVariant::Primary,
                        "Previous"
                    }
                }
                div {
                    class: page_css,
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
