use dioxus::prelude::*;
use dioxus_material::*;

#[derive(Debug, Clone, Copy, Props, PartialEq)]
pub struct Props {
    pub name: Signal<String>,
    pub close_game: Callback,
}

#[component]
pub fn GameButton(mut props: Props) -> Element {
    let onclick_change_name = move |_| {
        *props.name.write() += "a";
        props.close_game.call(());
    };

    rsx! {
        div { margin: "10px",
            Button { height: "100px", onpress: onclick_change_name, "{props.name}" }
        }
    }
}
