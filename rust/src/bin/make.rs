use std::env::current_dir;
use std::fs;
use std::path::Path;
use std::process::exit;
use std::process::Command;
use std::process::Stdio;

fn main() {
    let target_release_folder_path = Path::new(&current_dir().unwrap())
        .join("target")
        .join("release");
    if target_release_folder_path.exists() {
        fs::remove_dir_all(target_release_folder_path).unwrap();
    }
    let is_ok = Command::new("cargo")
        .args(["build", "--release"])
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
