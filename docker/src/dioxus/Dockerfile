FROM rockylinux/rockylinux:9.6.20250531 AS first_docker

LABEL maintainer="zdu.strong@gmail.com"

# Use the google cloud mirror as the dnf source
RUN sed -e 's|^mirrorlist=|#mirrorlist=|g' \
    -e 's|^#baseurl=http://dl.rockylinux.org/$contentdir|baseurl=https://rocky-linux-australia-southeast1.production.gcp.mirrors.ctrliq.cloud/pub/rocky|g' \
    -i.bak \
    /etc/yum.repos.d/rocky*.repo
RUN dnf makecache

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
RUN dnf install -y procps-ng
RUN dnf install -y xorg-x11-server-Xvfb
RUN dnf install -y gtk3-devel
ENV PATH="${PATH}:/root/.cargo/bin"
RUN curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y
RUN rustup update
RUN rustup toolchain install stable
RUN cargo install stylance-cli
RUN cargo install dioxus-cli --locked

# cargo build
COPY . /all_code
WORKDIR /all_code/dioxus
RUN rm -rf ./target
WORKDIR /all_code/dioxus-cypress
RUN npm test
WORKDIR /all_code/dioxus
RUN cargo make

# copy build folder
FROM first_docker
COPY --from=second_docker /all_code/dioxus/target/dx/rust-app/release/web/public /build

EXPOSE 443

# start server
ENTRYPOINT ["/bin/bash", "-c", "serve --single --cors --no-clipboard --no-port-switching --no-request-logging --listen=443 build"]
