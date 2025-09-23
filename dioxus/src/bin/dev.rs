use std::process::Command;

fn main() {
    Command::new("dx")
        .args(["serve", "--hot-reload=true", "--open=true", "--port=3000", "--platform=web"])
        .status()
        .unwrap();
}
