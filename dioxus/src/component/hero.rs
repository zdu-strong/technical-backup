use std::time::Duration;

use crate::component::game_button::GameButton;
use crate::component::game_input::GameInput;
use crate::model::cat_model::CatModel;
use crate::model::user_model::UserModel;
use chrono::Local;
use dioxus::logger::tracing::info;
use dioxus::prelude::*;
use dioxus_material::Button;
use rust_decimal::prelude::*;
use tokio_with_wasm::alias::time::sleep;
use uuid::Uuid;

const HEADER_SVG: Asset = asset!("/assets/header.svg");

#[component]
pub fn Hero() -> Element {
    let mut cat = use_signal(|| CatModel {
        id: Signal::new(Uuid::new_v4().to_string()),
        name: Signal::new("Tom".to_string()),
    });

    use_future(|| async {
        sleep(Duration::from_millis(1)).await;
        let ref mut user_list = vec![UserModel {
            id: Signal::new(Uuid::new_v4().to_string()),
            name: Signal::new("Tom".to_string()),
            money: Signal::new(dec!(100.05123)),
            create_date: Signal::new(Local::now()),
            update_date: Signal::new(Local::now()),
        }];
        let ref mut json_string = serde_json::to_string(user_list).unwrap();
        info!("user_list_json_string: {:?}", json_string);
        let ref mut user_list: Vec<UserModel> = serde_json::from_str(json_string).unwrap();
        info!("user_list: {:?}", user_list);
    });

    let onpress_hero = move |_| {};

    let onclick_change_name = move |_| async move {
        *cat.write().name.write() += "a";
    };

    rsx! {
        div { id: "hero",
            img { id: "header", src: HEADER_SVG }
            div { margin: "10px", margin_bottom: "10px",
                Button { height: "100px", onpress: onpress_hero, "who are you?" }
            }
            GameButton { name: cat.read().name, onclick_change_name }
            GameInput { name: cat.read().name }
        }
    }
}
