use dioxus::prelude::*;

#[component]
pub fn GameInput(name: ReadSignal<Signal<String>>) -> Element {
    let oninput_name = move |e: FormEvent| {
        *name.read().clone().write() = e.value();
    };

    rsx! {
        div {
            margin: "10px",
            input {
                width: "100%",
                value: name,
                onchange: oninput_name,
            }
        }
    }
}
