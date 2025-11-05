use dioxus::prelude::*;
use dioxus_core::Event;
use dioxus_material::*;

#[component]
pub fn GameButton(
    name: ReadOnlySignal<Signal<String>>,
    onclick_change_name: EventHandler<Event<MouseData>>,
) -> Element {
    rsx! {
        div {
            margin: "10px",
            Button {
                height: "100px",
                onpress: onclick_change_name,
                "{name}"
            }
        }
    }
}
