use dioxus::prelude::*;
use dioxus_core::Event;

#[component]
pub fn GameButton(
    name: ReadSignal<Signal<String>>,
    onclick_change_name: EventHandler<Event<MouseData>>,
) -> Element {
    rsx! {
        div {
            margin: "10px",
            button {
                height: "100px",
                onclick: onclick_change_name,
                "{name}"
            }
        }
    }
}
