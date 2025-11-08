use dioxus::prelude::*;
use lumen_blocks::components::input::Input;
use lumen_blocks::components::label::Label;

#[component]
pub fn GameInput(name: ReadSignal<Signal<String>>) -> Element {
    let oninput_name = move |e: FormEvent| {
        *name.read().clone().write() = e.value();
    };

    rsx! {
        div {
            class: "w-full max-w-md space-y-4",
            margin: "10px",
            div {
                class: "space-y-1",
                Label {
                    for_id: Some("optional-field".to_string()),
                    "Please Enter Your Name:"
                }
                Input {
                    id: Some("optional-field".to_string()),
                    full_width: true,
                    value: name,
                    on_change: oninput_name
                }
            }
        }
    }
}
