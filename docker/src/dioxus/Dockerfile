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
RUN dnf upgrade -y
RUN dnf install -y gcc
RUN dnf install -y openssl-devel
ENV PATH="${PATH}:/root/.cargo/bin"
RUN curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y --profile minimal --default-toolchain nightly
RUN rustup update
RUN rustup target add wasm32-unknown-unknown
RUN cargo install dioxus-cli --locked

# cargo build
COPY . /all_code
WORKDIR /all_code/dioxus
RUN cargo make

# copy build folder
FROM first_docker
COPY --from=second_docker /all_code/dioxus/target/dx/rust-app/release/web/public /build

EXPOSE 443

# start server
ENTRYPOINT ["/bin/bash", "-c", "serve --single --cors --no-clipboard --no-port-switching --no-request-logging --listen=443 build"]
