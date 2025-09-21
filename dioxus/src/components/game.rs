use dioxus::prelude::*;

#[derive(Props, PartialEq, Clone, Debug, Default)]
pub struct GameProps {
    pub name: Signal<String>,
}

#[component]
pub fn Game(mut props: GameProps) -> Element {
    let change_name = move |_| {
        *props.name.write() += "a";
    };

    let onchange_name = move |e: Event<FormData>| {
        *props.name.write() = e.value();
    };

    rsx! {
        button {
            key: name,
            width: "400px",
            height: "100px",
            "data-style": "destructive",
            color: "red",
            background_color: "orange",
            onclick: change_name,
            "{props.name}"
        }
        input {
            padding_top: "20px",
            padding_bottom: "20px",
            oninput: onchange_name,
            value: props.name
        }
    }
}
