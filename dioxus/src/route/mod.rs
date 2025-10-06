use crate::components::navbar::Navbar;
use crate::route::blog_page::BlogPage;
use crate::route::home_page::HomePage;
use dioxus::prelude::*;
pub mod blog_page;
pub mod home_page;

#[derive(Debug, Clone, Copy, Routable, PartialEq)]
#[rustfmt::skip]
pub enum Route {
    #[layout(Navbar)]
    #[route("/")]
    HomePage {},

    #[route("/blog/:id")]
    BlogPage { id: i128 },
}
