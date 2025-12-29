use std::env::args;
use std::env::current_dir;
use std::fs;
use std::path::Path;
use std::process::exit;
use std::process::Command;
use std::process::Stdio;

const DO_NOT_CARGO_UPGRADE: &str = "--do-not-cargo-upgrade";

fn main() {
    let command_arg_list = args();
    let target_dx_folder_path = Path::new(&current_dir().unwrap()).join("target").join("dx");
    if target_dx_folder_path.exists() {
        fs::remove_dir_all(target_dx_folder_path).unwrap();
    }
    let is_ok = Command::new("rustup")
        .args(["update"])
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
        .args(["toolchain", "install", "stable"])
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
    let is_ok = Command::new("cargo")
        .args(["binstall", "cargo-edit"])
        .current_dir(current_dir().unwrap())
        .stdin(Stdio::inherit())
        .stdout(Stdio::inherit())
        .stderr(Stdio::inherit())
        .output()
        .is_ok();
    if !is_ok {
        exit(1);
    }
    if !command_arg_list
        .into_iter()
        .any(|arg| arg == DO_NOT_CARGO_UPGRADE)
    {
        let is_ok = Command::new("cargo")
            .args(["upgrade"])
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
    let is_ok = Command::new("cargo")
        .args(["update"])
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
