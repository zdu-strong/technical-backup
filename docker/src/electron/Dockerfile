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

# run test
FROM first_docker AS second_docker
RUN dnf module install -y nodejs:24
RUN dnf install -y git
RUN dnf install -y procps-ng
RUN dnf install -y xorg-x11-server-Xvfb
RUN dnf install -y gtk3-devel
# RUN dnf install -y gtk2-devel
# RUN dnf install -y libnotify-devel nss libXScrnSaver alsa-lib
# RUN dnf install -y gcc-c++ cairo-devel pango-devel libjpeg-turbo-devel giflib
# RUN dnf install -y mesa-dri-drivers Xvfb libXi-devel libXinerama-devel libX11-devel
COPY . /all-code
RUN rm -rf /all-code/electron/node_modules
RUN rm -rf /all-code/electron-playwright/node_modules
WORKDIR /all-code/electron-playwright
RUN xvfb-run npm test
WORKDIR /all-code/electron
RUN npm run make
RUN mv output/*.AppImage output/my-app.AppImage

# copy appImage
FROM first_docker
COPY --from=second_docker /all-code/electron/output/my-app.AppImage /my-app.AppImage

# start server
ENTRYPOINT ["/bin/bash"]
