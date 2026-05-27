use base64::Engine;
use base64::engine::general_purpose::STANDARD;
use dioxus::prelude::*;

pub const HEADER_SVG_IMAGE: GlobalSignal<String> = GlobalSignal::new(|| {
    format!(
        "data:image/svg+xml;base64,{}",
        STANDARD.encode(include_bytes!("../../assets/image/header.svg"))
    )
});
