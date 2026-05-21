use std::env::current_dir;
use std::process::Command;
use std::process::Stdio;
use std::process::exit;

const DO_NOT_CARGO_UPGRADE: &str = "--do-not-cargo-upgrade";

fn main() {
    install_dioxus_cli();
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
    let is_ok = Command::new("cargo")
        .args(["up", "--", DO_NOT_CARGO_UPGRADE])
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
