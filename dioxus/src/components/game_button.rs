use dioxus::prelude::*;
use dioxus_core::Event;
use dioxus_material::*;

#[derive(Debug, Clone, Copy, Props, PartialEq)]
pub struct Props {
    pub name: Signal<String>,
    pub onclick_change_name: EventHandler<Event<MouseData>>,
}

#[component]
pub fn GameButton(props: Props) -> Element {
    rsx! {
        div { margin: "10px",
            Button { height: "100px", onpress: props.onclick_change_name, "{props.name}" }
        }
    }
}
