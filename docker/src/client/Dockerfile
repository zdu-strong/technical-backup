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
RUN dnf module install -y nodejs:24

# install serve
RUN npm install --location=global serve

# run test
FROM first_docker AS second_docker
RUN dnf upgrade -y
RUN dnf install -y git
RUN dnf install -y java-25-openjdk-devel
ENV JAVA_HOME="/usr/lib/jvm/java-25-openjdk"
RUN dnf install -y procps-ng
RUN dnf install -y xorg-x11-server-Xvfb
RUN dnf install -y gtk3-devel
COPY . /all_code
WORKDIR /all_code/springboot
RUN chmod +x mvn
WORKDIR /all_code/react-cypress
RUN rm -rf ../react/node_modules
RUN rm -rf ./node_modules
RUN npm test

# compile code
WORKDIR /all_code/react
RUN npm run build

# copy build folder
FROM first_docker
COPY --from=second_docker /all_code/react/build /build

EXPOSE 443

# start server
ENTRYPOINT ["/bin/bash", "-c", "node build/support_react_app_environment_variables.js && serve --single --cors --no-clipboard --no-port-switching --no-request-logging --listen=443 build"]