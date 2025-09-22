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
pub fn GameButton(mut props: GameProps) -> Element {

    let onclick_change_name = move |_| {
        *props.name.write() += "a";
    };

    rsx! {
        div {
            margin: "10px",
            Button {
                height: "100px",
                onpress: onclick_change_name,
                "{props.name}"
            }
        }
    }
}
