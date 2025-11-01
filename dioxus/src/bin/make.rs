use std::env::current_dir;
use std::fs::copy;
use std::fs::read_dir;
use std::path::Path;
use std::process::Command;

fn main() {
    let _ = Command::new("dx")
        .args(["bundle", "--release", "--web"])
        .current_dir(current_dir().unwrap())
        .status()
        .unwrap()
        .success();
    let assets_folder_path = Path::new(&current_dir().unwrap()).join("assets");
    let assets_folder = read_dir(assets_folder_path).unwrap();
    for assets_single_file in assets_folder {
        if assets_single_file.as_ref().unwrap().path().is_file() {
            let assets_target_file_path = Path::new(&current_dir().unwrap())
                .join("target/dx/rust-app/release/web/public")
                .join(assets_single_file.as_ref().unwrap().file_name());
            copy(
                assets_single_file.as_ref().unwrap().path(),
                assets_target_file_path,
            )
            .unwrap();
        }
    }
}
