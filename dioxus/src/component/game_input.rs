use daisy_rsx::*;
use dioxus::prelude::*;

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
                class: "space-y-4 flex flex-col",
                Input {
                    name: "name",
                    value: "{name}",
                    label: "Please Enter Your Name:",
                    onchange: oninput_name,
                }
            }
        }
    }
}
