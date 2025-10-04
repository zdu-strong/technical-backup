use std::env::current_dir;
use std::process::Command;
use std::net::TcpListener;
use std::net::SocketAddr;

fn main() {
    let port = 3000;
    if is_port_in_use(port) {
        println!("Port {} is already in use.", port);
        return;
    }
    let _ = Command::new("dx")
        .args([
            "serve",
            "--hot-reload=true",
            "--open=true",
            "--port=3000",
            "--platform=web",
            "--wsl-file-poll-interval=2",
        ])
        .current_dir(current_dir().unwrap())
        .status()
        .unwrap()
        .success();
}

fn is_port_in_use(port: u16) -> bool {
    let addr = SocketAddr::from(([127, 0, 0, 1], port));
    
    match TcpListener::bind(addr) {
        Ok(_) => false,
        Err(_) => true,
    }
}
