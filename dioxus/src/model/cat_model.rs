use dioxus::signals::Signal;
use serde::Deserialize;
use serde::Serialize;

#[derive(Debug, Clone, Copy, Default, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct CatModel {
    pub id: Signal<String>,
    pub name: Signal<String>,
}
