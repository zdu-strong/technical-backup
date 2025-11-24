use std::env::current_dir;
use std::fs;
use std::net::SocketAddr;
use std::net::TcpListener;
use std::path::Path;
use std::process::exit;
use std::process::Command;
use std::process::Stdio;

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
            "--platform",
            "web",
            "--open",
            "true",
            "--port",
            "3000",
            "--hot-patch",
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

fn install_dioxus_cli() {
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
        return;
    }
    let is_ok = Command::new("cargo")
        .args(["rise"])
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
