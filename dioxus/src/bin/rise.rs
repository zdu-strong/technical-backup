use std::env::current_dir;
use std::process::Command;
use std::fs;
use std::path::Path;

fn main() {
    let target_dx_folder_path = Path::new(&current_dir().unwrap()).join("target").join("dx");
    if target_dx_folder_path.exists() {
        fs::remove_dir_all(target_dx_folder_path).unwrap();
    }
    let _ = Command::new("rustup")
        .args(["update"])
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
    let _ = Command::new("cargo")
        .args(["binstall", "cargo-edit"])
        .current_dir(current_dir().unwrap())
        .status()
        .unwrap()
        .success();
    let _ = Command::new("cargo")
        .args(["upgrade"])
        .current_dir(current_dir().unwrap())
        .status()
        .unwrap()
        .success();
    let _ = Command::new("cargo")
        .args(["update"])
        .current_dir(current_dir().unwrap())
        .status()
        .unwrap()
        .success();
}
