use std::env::args;
use std::env::current_dir;
use std::fs;
use std::path::Path;
use std::process::Command;
use std::process::Stdio;
use std::process::exit;
use std::str::from_utf8;

const DO_NOT_CARGO_UPGRADE: &str = "--do-not-cargo-upgrade";

fn main() {
    remove_target_dir();
    if is_do_not_cargo_upgrade_and_exit() {
        return;
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
    cargo_upgrade();
    install_dioxus_cli();
}

fn is_do_not_cargo_upgrade_and_exit() -> bool {
    if is_do_not_cargo_upgrade() {
        if Command::new("dx")
            .args(["--version"])
            .current_dir(current_dir().unwrap())
            .stdin(Stdio::inherit())
            .stdout(Stdio::piped())
            .stderr(Stdio::piped())
            .output()
            .is_ok_and(|s| {
                let stdout_of_version = s.stdout;
                let start_content = "dioxus ";
                let end_content = " (";
                let version_of_dioxus_cli_now =
                    vec![String::from(from_utf8(&stdout_of_version).unwrap())]
                        .iter()
                        .filter(|s| s.starts_with(start_content))
                        .map(|s| {
                            let start_index =
                                s.find(start_content).unwrap() + start_content.chars().count();
                            let end_index = s.find(end_content).unwrap();
                            let version_text = s
                                .chars()
                                .skip(start_index)
                                .take(end_index - start_index)
                                .collect::<String>();
                            return version_text;
                        })
                        .collect::<String>();
                if version_of_dioxus_cli_now.chars().count() == 0 {
                    panic!("The version info for dioxus-cli does not exist");
                }

                return version_of_dioxus_cli_now == get_version_of_dioxus_cli_of_toml();
            })
        {
            return true;
        }
    }
    return false;
}

fn get_version_of_dioxus_cli_of_toml() -> String {
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
    return version_of_dioxus_cli;
}

fn install_dioxus_cli() {
    let version_of_dioxus_cli = get_version_of_dioxus_cli_of_toml();
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

fn is_do_not_cargo_upgrade() -> bool {
    let command_arg_list = args();
    return command_arg_list
        .into_iter()
        .any(|arg| arg == DO_NOT_CARGO_UPGRADE);
}

fn cargo_upgrade() {
    if is_do_not_cargo_upgrade() {
        return;
    }
    let is_ok = Command::new("cargo")
        .args([
            "install",
            "--locked",
            "--version",
            "1.20.0",
            "cargo-binstall",
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
