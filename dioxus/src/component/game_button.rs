use dioxus::prelude::*;
use dioxus_core::Event;
use lumen_blocks::components::button::Button;
use lumen_blocks::components::button::ButtonVariant;

#[component]
pub fn GameButton(
    name: ReadSignal<Signal<String>>,
    onclick_change_name: Callback<Event<MouseData>>,
) -> Element {
    rsx! {
        div {
            margin: "10px",
            Button {
                height: "100px",
                width: "200px",
                variant: ButtonVariant::Primary,
                on_click: onclick_change_name,
                "{name}"
            }
        }
    }
}
