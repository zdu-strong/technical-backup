use dioxus::prelude::*;
use serde::Deserialize;
use serde::Serialize;
use dioxus_material::*;

#[derive(Debug, Clone, Serialize, Deserialize, Default, Props, PartialEq)]
#[serde(rename_all = "camelCase")]
pub struct GameProps {
    pub name: Signal<String>,
}

#[component]
pub fn Game(mut props: GameProps) -> Element {

    let onchange_name = move |e: Event<FormData>| {
        *props.name.write() = e.value();
    };

    let onclick_change_name = move |_| {
         *props.name.write() += "a";
    };

    rsx! {
        div {
            margin: "10px",
            margin_bottom: "10px",
            Button {
                height: "100px",
                onpress: onclick_change_name,
                "{props.name}"
            }
        }
        input {
            padding_top: "20px",
            padding_bottom: "20px",
            oninput: onchange_name,
            value: props.name
        }
    }
}
