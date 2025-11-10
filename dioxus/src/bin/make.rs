use std::env::current_dir;
use std::process::Command;
use std::fs;
use std::path::Path;

fn main() {
    let target_dx_folder_path = Path::new(&current_dir().unwrap()).join("target").join("dx");
    fs::remove_dir_all(target_dx_folder_path).unwrap();
    let _ = Command::new("dx")
        .args(["bundle", "--release", "--web"])
        .current_dir(current_dir().unwrap())
        .status()
        .unwrap()
        .success();
}
