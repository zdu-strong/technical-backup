use std::env::current_dir;
use std::fs;
use std::path::Path;
use std::process::exit;
use std::process::Command;
use std::process::Stdio;

const DO_NOT_CARGO_UPGRADE: &str = "--do-not-cargo-upgrade";

fn main() {
    remove_target_dir();
    install_dioxus_cli();
    generate_stylance_css_file();
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
        .args(["rise", DO_NOT_CARGO_UPGRADE])
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

fn remove_target_dir() {
    let target_dx_folder_path = Path::new(&current_dir().unwrap()).join("target").join("dx");
    if target_dx_folder_path.exists() {
        fs::remove_dir_all(target_dx_folder_path).unwrap();
    }
}

fn generate_stylance_css_file() {
    let stylance_css_file_path = Path::new(&current_dir().unwrap())
        .join("assets")
        .join("stylance.bundled.css");
    let stylance_css_file_one_content = fs::read_to_string(&stylance_css_file_path).unwrap();
    let is_ok = Command::new("stylance")
        .args([
            "--folder",
            "./assets/styling",
            "--output-file",
            "./assets/stylance.bundled.css",
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
    let stylance_css_file_two_content = fs::read_to_string(stylance_css_file_path).unwrap();
    if stylance_css_file_one_content == stylance_css_file_two_content {
        let _ = Command::new("git")
            .args(["add", "./assets/stylance.bundled.css"])
            .current_dir(current_dir().unwrap())
            .stdin(Stdio::inherit())
            .stdout(Stdio::null())
            .stderr(Stdio::inherit())
            .output()
            .is_ok();
    }
}
