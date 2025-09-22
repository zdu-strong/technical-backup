use crate::components::game::Game;
use dioxus::logger::tracing::info;
use dioxus::prelude::*;
use dioxus_material::Button;
use serde::Deserialize;
use serde::Serialize;
use uuid::Uuid;

const HEADER_SVG: Asset = asset!("/assets/header.svg");

#[derive(Debug, Clone, Serialize, Deserialize, Default, Props, PartialEq)]
#[serde(rename_all = "camelCase")]
pub struct CatModel {
    pub id: Signal<String>,
    pub name: Signal<String>,
}

#[component]
pub fn Hero() -> Element {
    let cat = use_signal(|| CatModel {
        id: Signal::new(Uuid::new_v4().to_string()),
        name: Signal::new("Tom".to_string()),
    });

    use_future(move || async move {
        let ref mut cat_list = vec![CatModel {
            id: Signal::new(Uuid::new_v4().to_string()),
            name: Signal::new("Tom".to_string()),
        }];
        let ref mut json_string = serde_json::to_string(cat_list).unwrap();
        let ref mut cat_list: Vec<CatModel> = serde_json::from_str(json_string).unwrap();
        info!("{:?}", cat_list);
    });

    let onpress_hero = |_| {};

    rsx! {
        // We can create elements inside the rsx macro with the element name followed by a block of attributes and children.
        div {
            // Attributes should be defined in the element before any children
            id: "hero",
            // After all attributes are defined, we can define child elements and components
            img { src: HEADER_SVG, id: "header" }
            div {
                margin: "10px",
                margin_bottom: "10px",
                    Button {
                    height: "100px",
                    onpress: onpress_hero,
                    "who are you?"
                }
            }
            Game {
                name: cat.read().name,
            }
            Game {
                name: cat.read().name,
            }
        }
    }
}
