use dioxus::prelude::*;
use dioxus_material::*;

#[derive(Debug, Clone, Copy, Default, Props, PartialEq)]
pub struct Props {
    pub name: Signal<String>,
}

#[component]
pub fn GameInput(mut props: Props) -> Element {
    let oninput_name = move |e: FormEvent| {
        *props.name.write() = e.value();
    };

    rsx! {
        div { margin: "10px",
            TextField {
                width: "100%",
                label: "name",
                value: props.name,
                onchange: oninput_name,
            }
        }
    }
}
