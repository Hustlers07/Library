#!/bin/sh
envsubst < /usr/share/nginx/html/assets/env-config.js \
  > /usr/share/nginx/html/assets/env-config.js.tmp && \
mv /usr/share/nginx/html/assets/env-config.js.tmp /usr/share/nginx/html/assets/env-config.js

exec "$@"
