use std::env::current_dir;
use std::fs;
use std::path::Path;
use std::process::exit;
use std::process::Command;
use std::process::Stdio;

fn main() {
    remove_target_dir();
    let is_ok = Command::new("cargo")
        .args(["run", "--bin", "my", "--quiet"])
        .current_dir(current_dir().unwrap())
        .env("RUST_BACKTRACE", "1")
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
