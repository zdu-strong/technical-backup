use std::env::args;
use std::env::current_dir;
use std::fs;
use std::path::Path;
use std::process::Command;
use std::process::Stdio;
use std::process::exit;

const DO_NOT_CARGO_UPGRADE: &str = "--do-not-cargo-upgrade";

fn main() {
    remove_target_dir();
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
    install_dioxus_cli();
    cargo_upgrade();
}

fn install_dioxus_cli() {
    let cargo_toml_path = String::from(
        Path::new(&current_dir().unwrap())
            .join("Cargo.toml")
            .to_str()
            .unwrap(),
    );
    let text_of_cargo_toml = fs::read_to_string(cargo_toml_path).unwrap();
    let start_content = "dioxus = { version = \"";
    let end_content = "\", features";
    let version_of_dioxus_cli = text_of_cargo_toml
        .lines()
        .filter(|s| s.starts_with(start_content))
        .map(|s| {
            let start_index = s.find(start_content).unwrap() + start_content.chars().count();
            let end_index = s.find(end_content).unwrap();
            let version_text = s
                .chars()
                .skip(start_index)
                .take(end_index - start_index)
                .collect::<String>();
            return version_text;
        })
        .collect::<String>();
    if version_of_dioxus_cli.chars().count() == 0 {
        panic!("The version info for dioxus-cli does not exist");
    }
    let is_ok = Command::new("cargo")
        .args([
            "install",
            "--locked",
            "--version",
            version_of_dioxus_cli.as_str(),
            "dioxus-cli",
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
}

fn cargo_upgrade() {
    let command_arg_list = args();
    if !command_arg_list
        .into_iter()
        .any(|arg| arg == DO_NOT_CARGO_UPGRADE)
    {
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
            .args(["binstall", "-y", "cargo-edit"])
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
