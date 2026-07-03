use crate::constant::server_constant::DioxusHookStatus;
use crate::constant::server_constant::SERVER_USER_INFO;
use derive_more::Display;
use dioxus::prelude::*;
use serde::Deserialize;
use serde::Serialize;
use serde_aux::prelude::*;
use serde_json::to_string_pretty;

#[derive(Debug, Display, Clone, Default, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
#[display("{}", to_string_pretty(self).unwrap())]
pub struct AppCheckPermissionModel {
    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub check_is_sign_in: bool,

    #[serde[default]]
    #[serde(deserialize_with = "deserialize_default_from_null")]
    pub check_is_not_sign_in: bool,
}

impl AppCheckPermissionModel {
    pub fn is_ready(&self, dioxus_hook_status: DioxusHookStatus) -> bool {
        if self.check_is_sign_in && SERVER_USER_INFO().access_token.read().trim().is_empty() {
            return false;
        }
        if self.check_is_not_sign_in && !SERVER_USER_INFO().access_token.read().trim().is_empty() {
            return false;
        }
        return dioxus_hook_status.ready && true;
    }
}
