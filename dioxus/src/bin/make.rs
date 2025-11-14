use std::env::current_dir;
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
        .stdout(Stdio::inherit())
        .stderr(Stdio::inherit())
        .output()
        .is_ok();
    if !is_ok {
        exit(1);
    }
    let is_ok = Command::new("dx")
        .args(["bundle", "--release", "--web"])
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
