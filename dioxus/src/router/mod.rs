use crate::component::navbar::Navbar;
use crate::router::blog_page::BlogPage;
use crate::router::home_page::HomePage;
use crate::router::not_found_page::NotFoundPage;
use dioxus::prelude::*;
pub mod blog_page;
pub mod home_page;
pub mod not_found_page;

#[derive(Debug, Clone, Routable, PartialEq)]
#[rustfmt::skip]
pub enum Route {
    #[layout(Navbar)]
    #[route("/")]
    HomePage {},

    #[route("/blog/:id")]
    BlogPage { id: i128 },

    #[route("/:..route")]
    NotFoundPage { route: Vec<String> },
}
