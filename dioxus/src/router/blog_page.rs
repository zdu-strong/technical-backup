use crate::component::blog::blog::Blog;
use crate::component::system_menu::check_page_permission_component::CheckPagePermissionComponent;
use dioxus::prelude::*;

#[component]
pub fn BlogPage(id: ReadSignal<i128>) -> Element {
    rsx! {
        CheckPagePermissionComponent {
            check_is_not_sign_in: true,
            Blog { id }
        }
    }
}
