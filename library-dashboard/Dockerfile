# Serve stage
FROM nginx:alpine

# Copy nginx config
COPY nginx.conf /etc/nginx/nginx.conf

# Copy pre-built application (built in CI pipeline)
COPY dist/library-dashboard/browser /usr/share/nginx/html

# Copy entrypoint script
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# Copy env-config.js template
COPY src/assets/env-config.js /usr/share/nginx/html/assets/env-config.js

ENTRYPOINT ["/entrypoint.sh"]

# Expose port
EXPOSE 80

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
