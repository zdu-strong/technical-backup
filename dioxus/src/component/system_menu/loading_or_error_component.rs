use dioxus::prelude::*;

#[component]
pub fn LoadingOrErrorComponent(
    ready: ReadSignal<bool>,
    error: ReadSignal<Option<Signal<Option<String>>>>,
    children: Element,
) -> Element {
    rsx! {
        if error().is_some_and(|error_one| !error_one().is_none()) {
            "error"
        } else if ready() {
            {children}
        } else {
            "loading"
        }
    }
}
