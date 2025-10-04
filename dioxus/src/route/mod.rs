use crate::route::blog::Blog;
use crate::route::home::Home;
use crate::route::navbar::Navbar;
use dioxus::prelude::*;
pub mod blog;
pub mod home;
pub mod navbar;

#[derive(Debug, Clone, Copy, Routable, PartialEq)]
#[rustfmt::skip]
pub enum Route {
    #[layout(Navbar)]
    #[route("/")]
    Home {},

    #[route("/blog/:id")]
    Blog { id: i32 },
}
