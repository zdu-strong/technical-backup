use daisy_rsx::*;
use dioxus::prelude::*;

#[component]
pub fn GameInput(name: ReadSignal<Signal<String>>) -> Element {
    rsx! {
        div {
            class: "w-full max-w-md space-y-4",
            margin: "10px",
            div {
                class: "space-y-4 flex flex-col",
                Input {
                    name: "name",
                    label: "Please Enter Your Name:",
                    bind_value: name(),
                }
            }
        }
    }
}
