FROM rockylinux/rockylinux:9.6.20250531 AS first_docker

LABEL maintainer="zdu.strong@gmail.com"

# support utf-8
RUN dnf install -y langpacks-en glibc-langpack-en
ENV LANG="en_US.UTF-8"

# run test
FROM first_docker AS second_docker
RUN dnf upgrade -y
RUN dnf module install -y nodejs:22
RUN dnf install -y git
RUN dnf install -y procps-ng
RUN dnf install -y xorg-x11-server-Xvfb
RUN dnf install -y gtk2-devel
RUN dnf install -y gtk3-devel
# RUN dnf install -y libnotify-devel nss libXScrnSaver alsa-lib
# RUN dnf install -y gcc-c++ cairo-devel pango-devel libjpeg-turbo-devel giflib
# RUN dnf install -y mesa-dri-drivers Xvfb libXi-devel libXinerama-devel libX11-devel
COPY . /all_code
WORKDIR /all_code/electron-playwright
RUN rm -rf ../electron/node_modules
RUN rm -rf ./node_modules
RUN xvfb-run npm test
RUN rm -rf ./node_modules
RUN rm -rf ../electron/node_modules

# copy all_code folder
FROM first_docker
COPY --from=second_docker /all_code/electron /electron
WORKDIR /electron

# start server
ENTRYPOINT ["/bin/bash"]
