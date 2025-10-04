use dioxus::prelude::*;
use dioxus_material::*;

#[component]
pub fn GameInput(name: Signal<String>) -> Element {
    let oninput_name = move |e: FormEvent| {
        *name.write() = e.value();
    };

    rsx! {
        div { margin: "10px",
            TextField {
                width: "100%",
                label: "name",
                value: name,
                onchange: oninput_name,
            }
        }
    }
}
