use dioxus_i18n::prelude::*;
use dioxus_i18n::unic_langid::langid;

const EN_US_JSON: &str = include_str!("../../../assets/i18n/en-US.ftl");

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
