use std::env::current_dir;
use std::process::Command;

fn main() {
    let _ = Command::new("dx")
        .args([
            "serve",
            "--hot-reload=true",
            "--open=true",
            "--port=3000",
            "--platform=web",
        ])
        .current_dir(current_dir().unwrap())
        .status()
        .unwrap()
        .success();
}
