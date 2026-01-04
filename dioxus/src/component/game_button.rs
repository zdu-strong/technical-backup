use daisy_rsx::*;
use dioxus::prelude::*;

#[component]
pub fn GameButton(
    name: ReadSignal<Signal<String>>,
    onclick_change_name: Callback<MouseEvent>,
) -> Element {
    rsx! {
        div {
            class: "flex flex-col space-y-4 w-full max-w-md",
            margin: "10px",
            Button {
                id: "answer",
                button_scheme: ButtonScheme::Secondary,
                onclick: onclick_change_name,
                "{name}"
            }
        }
    }
}
