use crate::components::game_button::GameButton;
use crate::components::game_input::GameInput;
use crate::model::cat_model::CatModel;
use dioxus::logger::tracing::info;
use dioxus::prelude::*;
use dioxus_material::Button;
use uuid::Uuid;

const HEADER_SVG: Asset = asset!("/assets/header.svg");

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

    let close_game = use_callback(|_| {});

    rsx! {
        div { id: "hero",
            img { id: "header", src: HEADER_SVG }
            div { margin: "10px", margin_bottom: "10px",
                Button { height: "100px", onpress: onpress_hero, "who are you?" }
            }
            GameButton { name: cat.read().name, close_game }
            GameInput { name: cat.read().name }
        }
    }
}
