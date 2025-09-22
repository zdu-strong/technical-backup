use dioxus::prelude::*;
use dioxus_material::*;
use serde::Deserialize;
use serde::Serialize;

#[derive(Debug, Clone, Serialize, Deserialize, Default, Props, PartialEq)]
#[serde(rename_all = "camelCase")]
pub struct GameProps {
    pub name: Signal<String>,
}

#[component]
pub fn GameInput(mut props: GameProps) -> Element {

    let oninput_name = move |e: FormEvent| {
        *props.name.write() = e.value();
    };

    rsx! {
        div {
            margin: "10px",
            TextField {
                width: "400px",
                label: "name",
                value: props.name,
                onchange: oninput_name
            }
        }
    }
}
