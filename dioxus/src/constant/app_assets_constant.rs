use dioxus::prelude::*;

pub const FAVICON: Asset = asset!("/assets/image/favicon.ico");
pub const TAILWIND_CSS: &str = include_str!("../../assets/tailwind.css");

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
