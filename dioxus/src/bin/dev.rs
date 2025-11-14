use std::env::current_dir;
use std::net::SocketAddr;
use std::net::TcpListener;
use std::process::Command;
use std::fs;
use std::path::Path;
use std::process::Stdio;
use std::process::exit;

fn main() {
    let target_dx_folder_path = Path::new(&current_dir().unwrap()).join("target").join("dx");
    if target_dx_folder_path.exists() {
        fs::remove_dir_all(target_dx_folder_path).unwrap();
    }
    let port = 3000;
    if is_port_in_use(port) {
        eprintln!("Port {} is already in use.", port);
        exit(1);
    }
    install_dioxus_cli();
    let is_ok = Command::new("stylance")
        .args([
            "--folder",
            "./assets/styling",
            "--output-file",
            "./assets/common/stylance/stylance.bundled.css",
            ".",
        ])
        .current_dir(current_dir().unwrap())
        .stdin(Stdio::inherit())
        .stdout(Stdio::null())
        .stderr(Stdio::inherit())
        .output()
        .is_ok();
    if !is_ok {
        exit(1);
    }
    let dioxus_command = Command::new("dx")
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
        .stdin(Stdio::inherit())
        .stdout(Stdio::inherit())
        .stderr(Stdio::inherit())
        .spawn();
    let stylance_command = Command::new("stylance")
        .args([
            "--watch",
            "--folder",
            "./assets/styling",
            "--output-file",
            "./assets/common/stylance/stylance.bundled.css",
            ".",
        ])
        .current_dir(current_dir().unwrap())
        .env("RUST_BACKTRACE", "1")
        .stdin(Stdio::inherit())
        .stdout(Stdio::null())
        .stderr(Stdio::inherit())
        .spawn();
    let is_ok = dioxus_command.unwrap().wait_with_output().is_ok()
        && stylance_command.unwrap().wait_with_output().is_ok();
    if !is_ok {
        exit(1);
    }
}

fn install_dioxus_cli() -> bool {
    if Command::new("dx")
        .args(["--version"])
        .current_dir(current_dir().unwrap())
        .stdin(Stdio::inherit())
        .stdout(Stdio::piped())
        .stderr(Stdio::piped())
        .output()
        .is_ok()
        && Command::new("stylance")
            .args(["--version"])
            .current_dir(current_dir().unwrap())
            .stdin(Stdio::inherit())
            .stdout(Stdio::piped())
            .stderr(Stdio::piped())
            .output()
            .is_ok()
    {
        return true;
    }
    let is_ok = Command::new("rustup")
        .args(["toolchain", "install", "nightly"])
        .current_dir(current_dir().unwrap())
        .stdin(Stdio::inherit())
        .stdout(Stdio::inherit())
        .stderr(Stdio::inherit())
        .output()
        .is_ok();
    if !is_ok {
        exit(1);
    }
    let is_ok = Command::new("rustup")
        .args(["target", "add", "wasm32-unknown-unknown"])
        .current_dir(current_dir().unwrap())
        .stdin(Stdio::inherit())
        .stdout(Stdio::inherit())
        .stderr(Stdio::inherit())
        .output()
        .is_ok();
    if !is_ok {
        exit(1);
    }
    let is_ok = Command::new("cargo")
        .args(["install", "stylance-cli"])
        .current_dir(current_dir().unwrap())
        .stdin(Stdio::inherit())
        .stdout(Stdio::inherit())
        .stderr(Stdio::inherit())
        .output()
        .is_ok();
    if !is_ok {
        exit(1);
    }
    let is_ok = Command::new("cargo")
        .args(["install", "cargo-binstall"])
        .current_dir(current_dir().unwrap())
        .stdin(Stdio::inherit())
        .stdout(Stdio::inherit())
        .stderr(Stdio::inherit())
        .output()
        .is_ok();
    if !is_ok {
        exit(1);
    }
    let is_ok = Command::new("cargo")
        .args(["binstall", "dioxus-cli"])
        .current_dir(current_dir().unwrap())
        .stdin(Stdio::inherit())
        .stdout(Stdio::inherit())
        .stderr(Stdio::inherit())
        .output()
        .is_ok();
    if !is_ok {
        exit(1);
    }
    true
}

fn is_port_in_use(port: u16) -> bool {
    let addr = SocketAddr::from(([127, 0, 0, 1], port));

    match TcpListener::bind(addr) {
        Ok(_) => false,
        Err(_) => true,
    }
}
