FROM rockylinux/rockylinux:9.7.20251123 AS first_docker

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
RUN dnf module install -y nodejs:24

# install serve
RUN npm install --location=global serve

# install pm2
RUN npm install --location=global pm2

FROM first_docker AS second_docker

# install cargo
RUN dnf install -y gcc
ENV PATH="${PATH}:/root/.cargo/bin"
RUN curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh -s -- -y

# install git
RUN dnf install -y git

# cargo build
RUN dnf install -y openssl-devel
RUN dnf install -y procps-ng
RUN dnf install -y xorg-x11-server-Xvfb
RUN dnf install -y gtk3-devel
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
COPY --from=second_docker /all_code/dioxus/target/dx/rust-app/release/web/public/pm2-serve.js /pm2-serve.js

EXPOSE 443

# start server
ENTRYPOINT ["/bin/bash", "-c", "pm2 --no-daemon --instances max start pm2-serve.js"]
