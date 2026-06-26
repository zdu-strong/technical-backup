use base64::Engine;
use base64::engine::general_purpose::STANDARD;
use dioxus::prelude::*;

pub const FAVICON: Asset = asset!("/assets/image/favicon.ico");
pub const TAILWIND_CSS: &str = include_str!("../../assets/tailwind.css");
pub const EN_US_JSON: &str = include_str!("../../assets/i18n/en-US.ftl");
pub const HEADER_SVG_IMAGE: GlobalSignal<String> = GlobalSignal::new(|| {
    format!(
        "data:image/svg+xml;base64,{}",
        STANDARD.encode(include_bytes!("../../assets/image/header.svg"))
    )
});

const _: Asset = asset!(
    "/assets/common/jetbrains-mono-font/jetbrains-mono.css",
    AssetOptions::css().with_static_head(true)
);
const _: Asset = asset!(
    "/assets/common/app/app.css",
    AssetOptions::css().with_static_head(true)
);
const _: Asset = asset!(
    "/assets/common/daisyui/v4.12.24/daisyui.css",
    AssetOptions::css().with_static_head(true)
);
