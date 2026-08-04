#!/bin/bash
set -eux

apt-get update

apt-get install -y \
docker.io \
docker-compose-v2 \
git \
curl \
unzip

systemctl enable docker
systemctl start docker

usermod -aG docker ubuntu

mkdir -p /opt/sentinel

cat >/opt/sentinel/.env <<EOF
API_KEY=${api_key}
EOF

chown ubuntu:ubuntu /opt/sentinel/.env
chmod 600 /opt/sentinel/.env