#!/bin/bash
# Exit immediately if a command fails
set -euo pipefail

# ─────────────────────────────────────────
# K3s uses its own kubeconfig by default
# ─────────────────────────────────────────
export KUBECONFIG=${KUBECONFIG:-/etc/rancher/k3s/k3s.yaml}

# Check if k3s is running
if ! sudo k3s kubectl get nodes &>/dev/null; then
  echo "❌ Error: k3s is not running."
  echo "👉 Please start k3s with: sudo systemctl start k3s"
  exit 1
fi

# ─────────────────────────────────────────
# K3s uses containerd, not Docker.
# To use local images, import them via:
#   sudo k3s ctr images import image.tar
# Or build with nerdctl:
#   sudo nerdctl build -t myapp:latest .
# ─────────────────────────────────────────

# First argument is the deployment name
DEPLOYMENT_NAME=${1:-}
if [ -z "$DEPLOYMENT_NAME" ]; then
  echo "❌ Error: Deployment name not provided."
  echo "👉 Usage: ./deploy.sh <deployment-name>"
  exit 1
fi

echo "🚀 Deploying app to deployment: $DEPLOYMENT_NAME"

# Ensure namespace exists (idempotent)
sudo k3s kubectl create namespace library-dev --dry-run=client -o yaml | sudo k3s kubectl apply -f -

# Apply ConfigMaps and Secrets if they exist
[ -f ./dev/configmap.yaml ] && sudo k3s kubectl apply -f ./dev/configmap.yaml -n library-dev
[ -f ./dev/secrets.yaml ]   && sudo k3s kubectl apply -f ./dev/secrets.yaml   -n library-dev

# Apply the Kubernetes deployment manifest
DEPLOYMENT_FILE="./dev/${DEPLOYMENT_NAME}-deployment.yaml"
if [ ! -f "$DEPLOYMENT_FILE" ]; then
  echo "❌ Error: Deployment manifest not found at $DEPLOYMENT_FILE"
  exit 1
fi

sudo k3s kubectl apply -f "$DEPLOYMENT_FILE" -n library-dev

# Extract deployment name from the YAML file using grep and sed
ACTUAL_DEPLOYMENT_NAME=$(grep -A 2 "^kind: Deployment" "$DEPLOYMENT_FILE" \
  | grep "name:" | head -1 \
  | sed 's/.*name: *//;s/ *#.*//')

# Fallback: if extraction fails, try using kubectl
if [ -z "$ACTUAL_DEPLOYMENT_NAME" ]; then
  ACTUAL_DEPLOYMENT_NAME=$(sudo k3s kubectl get deployment -n library-dev \
    -o jsonpath="{.items[0].metadata.name}")
fi

# Final fallback: use the argument if all else fails
if [ -z "$ACTUAL_DEPLOYMENT_NAME" ]; then
  ACTUAL_DEPLOYMENT_NAME=$DEPLOYMENT_NAME
fi

echo "📦 Deployment : $ACTUAL_DEPLOYMENT_NAME"
echo "📂 Namespace  : library-dev"

# Restart rollout to ensure fresh pods with latest image
sudo k3s kubectl rollout restart deployment/"$ACTUAL_DEPLOYMENT_NAME" -n library-dev

# Wait for rollout to complete
sudo k3s kubectl rollout status deployment/"$ACTUAL_DEPLOYMENT_NAME" -n library-dev --timeout=5m

echo "✅ Done! Your app should now be deployed to k3s in namespace: library-dev"

# Checking pods
echo "🔍 Checking pods..."
sudo k3s kubectl get pods -n library-dev