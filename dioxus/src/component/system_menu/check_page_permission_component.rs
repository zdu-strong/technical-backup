use crate::component::system_menu::loading_or_error_component::LoadingOrErrorComponent;
use crate::constant::server_constant::use_multiple_query;
use crate::model::app_check_permission_model::AppCheckPermissionModel;
use dioxus::prelude::*;

#[component]
pub fn CheckPagePermissionComponent(
    children: Element,
    check_is_sign_in: ReadSignal<Option<bool>>,
    check_is_not_sign_in: ReadSignal<Option<bool>>,
) -> Element {
    let mut app_check_permission_model = use_context::<Signal<AppCheckPermissionModel>>();

    let dioxus_hook_status = use_multiple_query(move || async move {
        *app_check_permission_model.write() = AppCheckPermissionModel {
            check_is_not_sign_in: check_is_not_sign_in()
                .unwrap_or(!check_is_sign_in().unwrap_or_default()),
            check_is_sign_in: check_is_sign_in().unwrap_or_default(),
        };
    });

    rsx! {
        LoadingOrErrorComponent {
            ready: app_check_permission_model().is_ready(dioxus_hook_status.ready),
            error: dioxus_hook_status.error,
            {children}
        }
    }
}
