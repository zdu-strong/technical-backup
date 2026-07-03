use crate::component::home::hero::Hero;
use crate::component::system_menu::check_page_permission_component::CheckPagePermissionComponent;
use dioxus::prelude::*;

#[component]
pub fn HomePage() -> Element {
    rsx! {
        CheckPagePermissionComponent {
            check_is_not_sign_in: true,
            Hero {}
        }
    }
}
