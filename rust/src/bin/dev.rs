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
        .env("RUST_BACKTRACE", "full")
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
    let target_dx_folder_path = Path::new(&current_dir().unwrap()).join("target");
    if target_dx_folder_path.exists() {
        fs::remove_dir_all(target_dx_folder_path).unwrap();
    }
}
