use std::env::current_dir;
use std::process::Command;
use std::fs;
use std::path::Path;
use std::process::Stdio;

fn main() {
    let target_dx_folder_path = Path::new(&current_dir().unwrap()).join("target").join("dx");
    if target_dx_folder_path.exists() {
        fs::remove_dir_all(target_dx_folder_path).unwrap();
    }
    install_dioxus_cli();
    let _ = Command::new("dx")
        .args(["bundle", "--release", "--web"])
        .current_dir(current_dir().unwrap())
        .status()
        .unwrap()
        .success();
}

fn install_dioxus_cli() -> bool {
    if Command::new("dx")
        .args(["--version"])
        .current_dir(current_dir().unwrap())
        .stdout(Stdio::null())
        .status()
        .unwrap()
        .success()
    {
        return true;
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
    true
}
