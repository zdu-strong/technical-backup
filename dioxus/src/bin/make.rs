use std::env::current_dir;
use std::fs;
use std::path::Path;
use std::process::Command;
use std::process::Stdio;
use std::process::exit;

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
        .args(["rise", "--", DO_NOT_CARGO_UPGRADE])
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
    let target_folder_path = Path::new(&current_dir().unwrap()).join("target");
    if target_folder_path.exists() && target_folder_path.is_dir() {
        for dx_folder_path in target_folder_path.read_dir().unwrap() {
            if dx_folder_path
                .as_ref()
                .unwrap()
                .file_name()
                .to_str()
                .unwrap()
                != "debug"
            {
                if dx_folder_path.as_ref().unwrap().path().is_dir() {
                    fs::remove_dir_all(dx_folder_path.unwrap().path()).unwrap();
                } else if dx_folder_path.as_ref().unwrap().path().is_file() {
                    fs::remove_file(dx_folder_path.unwrap().path()).unwrap();
                }
            }
        }
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
