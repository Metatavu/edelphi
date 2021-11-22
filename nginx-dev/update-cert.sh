#!/bin/sh

docker run \
  --mount type=bind,source=$PWD/letsencrypt,target=/etc/letsencrypt \
  --mount type=bind,source=$PWD/cloudflare.ini,target=/root/.secrets/certbot/cloudflare.ini,readonly \
  certbot/dns-cloudflare certonly --non-interactive --agree-tos --email info@metatavu.fi --dns-cloudflare --dns-cloudflare-credentials /root/.secrets/certbot/cloudflare.ini -d dev.edelphi.org -d dev-auth.edelphi.org