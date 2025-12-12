use crate::component::game_button::GameButton;
use crate::component::game_input::GameInput;
use crate::model::cat_model::CatModel;
use crate::model::user_model::UserModel;
use bigdecimal::BigDecimal;
use chrono::Local;
use daisy_rsx::*;
use dioxus::logger::tracing::info;
use dioxus::prelude::*;
use std::str::FromStr;
use std::time::Duration;
use tokio_with_wasm::alias::time::sleep;
use uuid::Uuid;

const HEADER_SVG: Asset = asset!("/assets/image/header.svg");

#[component]
pub fn Hero() -> Element {
    let mut cat = use_signal(|| CatModel {
        id: Signal::new(Uuid::new_v4().to_string()),
        name: Signal::new("Tom".to_string()),
    });

    let mut question_text = use_signal(|| "Who are you?".to_string());

    use_future(move || async move {
        sleep(Duration::from_millis(1)).await;
        let user_list = vec![UserModel {
            id: Signal::new(Uuid::new_v4().to_string()),
            username: Signal::new("Tom".to_string()),
            money: Signal::new(Some(BigDecimal::from_str("124122112112222222222222222222222222222222222222222222222222222222222222222234124123431342341221423212124.1234124").unwrap())),
            create_date: Signal::new(Local::now()),
            update_date: Signal::new(Local::now()),
            access_token: Signal::new("".to_string()),
        }];
        let json_string = serde_json::to_string(&user_list).unwrap();
        let _: Vec<UserModel> = serde_json::from_str(&json_string).unwrap();
        info!("user_list_json_string: {}", json_string);
    });

    let onpress_hero = move |_| {
        if *question_text.read() == "Who are you?" {
            *question_text.write() = "What's your name?".to_string();
        } else {
            *question_text.write() = "Who are you?".to_string();
        }
    };

    let onclick_change_name = move |_| async move {
        if cat.read().name.read().trim().starts_with("Tom") {
            *cat.write().name.write() = "Jerry".to_string();
        } else {
            *cat.write().name.write() = "Tom".to_string();
        }
    };

    rsx! {
        div {
            id: "hero",
            class: "flex flex-col flex-auto items-center",
            img {
                id: "header",
                style: "max-width: 1200px",
                src: HEADER_SVG,
            }
            div {
                class: "flex flex-col space-y-4 w-full max-w-md",
                margin: "10px",
                margin_bottom: "10px ",
                Button {
                    button_scheme: ButtonScheme::Secondary,
                    onclick: onpress_hero,
                    "{question_text}"
                }
            }
            GameButton {
                name: cat.read().name,
                onclick_change_name,
            }
            GameInput {
                name: cat.read().name,
            }
        }
    }
}
