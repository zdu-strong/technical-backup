use std::env::current_dir;
use std::net::SocketAddr;
use std::net::TcpListener;
use std::process;
use std::process::Command;
use std::fs;
use std::path::Path;

fn main() {
    let target_dx_folder_path = Path::new(&current_dir().unwrap()).join("target").join("dx");
    if target_dx_folder_path.exists() {
        fs::remove_dir_all(target_dx_folder_path).unwrap();
    }
    let port = 3000;
    if is_port_in_use(port) {
        eprintln!("Port {} is already in use.", port);
        process::exit(1);
    }
    let _ = Command::new("rustup")
        .args(["toolchain", "install", "nightly"])
        .current_dir(current_dir().unwrap())
        .status()
        .unwrap()
        .success();
    let _ = Command::new("rustup")
        .args(["target", "add", "wasm32-unknown-unknown"])
        .current_dir(current_dir().unwrap())
        .status()
        .unwrap()
        .success();
    let _ = Command::new("cargo")
        .args(["install", "cargo-binstall"])
        .current_dir(current_dir().unwrap())
        .status()
        .unwrap()
        .success();
    let _ = Command::new("cargo")
        .args(["binstall", "dioxus-cli"])
        .current_dir(current_dir().unwrap())
        .status()
        .unwrap()
        .success();
    let _ = Command::new("dx")
        .args([
            "serve",
            "--hot-patch",
            "--open",
            "true",
            "--port",
            "3000",
            "--web",
            "--wsl-file-poll-interval",
            "2",
        ])
        .current_dir(current_dir().unwrap())
        .env("RUST_BACKTRACE", "1")
        .status()
        .unwrap()
        .success();
}

fn is_port_in_use(port: u16) -> bool {
    let addr = SocketAddr::from(([127, 0, 0, 1], port));

    match TcpListener::bind(addr) {
        Ok(_) => false,
        Err(_) => true,
    }
}
