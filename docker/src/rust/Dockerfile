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

FROM first_docker AS second_docker

# install cargo
RUN dnf install -y cargo
ENV PATH="${PATH}:/root/.cargo/bin"

# install git
RUN dnf install -y git

# cargo build
COPY . /all_code
WORKDIR /all_code/rust
RUN cargo make

# cargo run
FROM first_docker
COPY --from=second_docker /all_code/rust/target/release/my /my
RUN /my

# start server
ENTRYPOINT ["/bin/bash"]
