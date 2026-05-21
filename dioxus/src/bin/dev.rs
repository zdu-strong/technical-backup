use std::env::current_dir;
use std::net::SocketAddr;
use std::net::TcpListener;
use std::process::Command;
use std::process::Stdio;
use std::process::exit;

const DO_NOT_CARGO_UPGRADE: &str = "--do-not-cargo-upgrade";

fn main() {
    check_port_has_been_use();
    install_dioxus_cli();
    let is_ok = Command::new("dx")
        .args([
            "serve",
            "--platform",
            "web",
            "--open",
            "true",
            "--port",
            "3000",
            "--hot-reload",
            "true",
            "--hot-patch",
        ])
        .current_dir(current_dir().unwrap())
        .env("RUST_BACKTRACE", "1")
        .stdin(Stdio::inherit())
        .stdout(Stdio::inherit())
        .stderr(Stdio::inherit())
        .output()
        .is_ok();
    if !is_ok {
        exit(1);
    }
}

fn install_dioxus_cli() {
    let is_ok = Command::new("cargo")
        .args(["up", "--", DO_NOT_CARGO_UPGRADE])
        .current_dir(current_dir().unwrap())
        .stdin(Stdio::inherit())
        .stdout(Stdio::inherit())
        .stderr(Stdio::inherit())
        .output()
        .is_ok();
    if !is_ok {
        exit(1);
    }
}

fn is_port_in_use(port: u16) -> bool {
    let addr = SocketAddr::from(([127, 0, 0, 1], port));

    match TcpListener::bind(addr) {
        Ok(_) => false,
        Err(_) => true,
    }
}

fn check_port_has_been_use() {
    let port = 3000;
    if is_port_in_use(port) {
        eprintln!("Port {} is already in use.", port);
        exit(1);
    }
}
