FROM rockylinux/rockylinux:9.6.20250531 AS first_docker

LABEL maintainer="zdu.strong@gmail.com"

# support utf-8
RUN dnf install -y langpacks-en glibc-langpack-en
ENV LANG="en_US.UTF-8"

# install nodejs
RUN dnf module install -y nodejs:22

# install serve
RUN npm install --location=global serve

FROM first_docker AS second_docker

# install git
RUN dnf install -y git

# install cargo
RUN dnf install -y epel-release
RUN dnf install -y gcc
RUN dnf -y groupinstall "Development Tools"
RUN dnf -y install openssl-devel
ENV CARGO_HOME=/root/.cargo
ENV RUSTUP_HOME=/root/.rustup
ENV PATH="${PATH}:${CARGO_HOME}/bin"
RUN curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y --profile minimal --default-toolchain stable
RUN rustup update
RUN rustup -q toolchain install stable
RUN rustup target add wasm32-unknown-unknown
ENV CARGO_TARGET_DIR=/root/cargo/dioxus
RUN cargo install dioxus-cli --locked
# RUN curl -L --proto '=https' --tlsv1.2 -sSf https://raw.githubusercontent.com/cargo-bins/cargo-binstall/main/install-from-binstall-release.sh | bash

# cargo build
COPY . /all_code
WORKDIR /all_code/dioxus
RUN dx bundle --release --platform=web

# cargo run
FROM first_docker
COPY --from=second_docker /all_code/dioxus/target/dx/rust-app/release/web/public /build
COPY --from=second_docker /all_code/dioxus/assets/serve.json /build/serve.json
COPY --from=second_docker /all_code/dioxus/assets/favicon.ico /build/favicon.ico

EXPOSE 443

# # start server
ENTRYPOINT ["/bin/bash", "-c", "serve --single --cors --no-clipboard --no-port-switching --no-request-logging --listen=443 build"]