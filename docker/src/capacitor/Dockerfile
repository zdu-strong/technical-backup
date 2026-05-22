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

# run test
FROM first_docker AS second_docker
RUN dnf install -y git
RUN dnf install -y procps-ng
RUN dnf install -y xorg-x11-server-Xvfb
RUN dnf install -y gtk3-devel
COPY . /all-code
RUN rm -rf /all-code/capacitor/node_modules
RUN rm -rf /all-code/capacitor-cypress/node_modules
WORKDIR /all-code/capacitor-cypress
RUN npm test
RUN rm -rf /all-code/capacitor/node_modules

# copy all-code folder
FROM first_docker
COPY --from=second_docker /all-code/capacitor /capacitor
WORKDIR /capacitor

# start server
ENTRYPOINT ["/bin/bash"]
