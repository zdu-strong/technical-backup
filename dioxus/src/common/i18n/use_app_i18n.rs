use crate::constant::app_assets_constant::EN_US_JSON;
use dioxus_i18n::prelude::*;
use dioxus_i18n::unic_langid::langid;

pub fn use_app_i18n() {
    use_init_i18n(|| {
        I18nConfig::new(langid!("en-US"))
            // implicit [`Locale`]
            .with_locale((
                // Embed
                langid!("en-US"),
                EN_US_JSON,
            ))
    });
}
