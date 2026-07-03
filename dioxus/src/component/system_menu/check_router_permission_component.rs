use crate::component::system_menu::loading_or_error_component::LoadingOrErrorComponent;
use crate::component::system_menu::system_menu::SystemMenu;
use crate::constant::server_constant::DioxusHookStatus;
use crate::model::app_check_permission_model::AppCheckPermissionModel;
use dioxus::prelude::*;

#[component]
pub fn CheckRouterPermissionComponent() -> Element {
    let app_check_permission_model = use_context_provider(|| {
        Signal::new(AppCheckPermissionModel {
            check_is_sign_in: false,
            check_is_not_sign_in: true,
        })
    });

    let is_ready = move || {
        let mut dioxus_hook_status = DioxusHookStatus::default();
        dioxus_hook_status.ready = true;
        return app_check_permission_model().is_ready(dioxus_hook_status);
    };

    use_future(move || async move {
        let _ = app_check_permission_model;
        let _ = is_ready();
    });

    rsx! {
        LoadingOrErrorComponent {
            ready: is_ready(),
            SystemMenu {}
        }
    }
}
