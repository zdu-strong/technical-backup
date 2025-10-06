use dioxus::prelude::*;
use dioxus_material::*;

#[component]
pub fn GameInput(name: ReadOnlySignal<Signal<String>>) -> Element {
    let oninput_name = move |e: FormEvent| {
        *(name.read().clone()).write() = e.value();
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
