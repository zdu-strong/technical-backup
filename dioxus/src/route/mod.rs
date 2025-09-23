use crate::route::blog::Blog;
use crate::route::home::Home;
use crate::route::navbar::Navbar;
use dioxus::prelude::*;
use strum_macros::AsRefStr;
use strum_macros::EnumIter;
pub mod blog;
pub mod home;
pub mod navbar;

#[derive(Debug, Clone, Copy, EnumIter, AsRefStr, Routable, PartialEq)]
#[rustfmt::skip]
pub enum Route {
    #[layout(Navbar)]
    #[route("/")]
    Home {},

    #[route("/blog/:id")]
    Blog { id: i32 },
}
