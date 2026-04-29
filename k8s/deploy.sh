#!/bin/bash

# Exit immediately if a command fails
set -euo pipefail

# Check if Minikube is running
if ! minikube status | grep -q "Running"; then
  echo "❌ Error: Minikube is not running."
  echo "👉 Please start Minikube with: minikube start"
  exit 1
fi

# Switch Docker to use Minikube's environment
eval "$(minikube docker-env)"

# First argument is the deployment name
DEPLOYMENT_NAME=${1:-}

if [ -z "$DEPLOYMENT_NAME" ]; then
  echo "❌ Error: Deployment name not provided."
  echo "👉 Usage: ./deploy.sh <deployment-name>"
  exit 1
fi

echo "🚀 Deploying app to deployment: $DEPLOYMENT_NAME"

# Ensure namespace exists (idempotent)
kubectl create namespace library-dev --dry-run=client -o yaml | kubectl apply -f -

# Apply ConfigMaps and Secrets if they exist
[ -f ./dev/configmap.yaml ] && kubectl apply -f ./dev/configmap.yaml -n library-dev
[ -f ./dev/secrets.yaml ] && kubectl apply -f ./dev/secrets.yaml -n library-dev

# Apply the Kubernetes deployment manifest
DEPLOYMENT_FILE="./dev/${DEPLOYMENT_NAME}-deployment.yaml"
if [ ! -f "$DEPLOYMENT_FILE" ]; then
  echo "❌ Error: Deployment manifest not found at $DEPLOYMENT_FILE"
  exit 1
fi

kubectl apply -f "$DEPLOYMENT_FILE" -n library-dev

# Extract deployment name from the YAML file using grep and sed
ACTUAL_DEPLOYMENT_NAME=$(grep -A 2 "^kind: Deployment" "$DEPLOYMENT_FILE" | grep "name:" | head -1 | sed 's/.*name: *//;s/ *#.*//')

# Fallback: if extraction fails, try using kubectl
if [ -z "$ACTUAL_DEPLOYMENT_NAME" ]; then
  ACTUAL_DEPLOYMENT_NAME=$(kubectl get deployment -n library-dev -o jsonpath="{.items[0].metadata.name}")
fi

# Final fallback: use the argument if all else fails
if [ -z "$ACTUAL_DEPLOYMENT_NAME" ]; then
  ACTUAL_DEPLOYMENT_NAME=$DEPLOYMENT_NAME
fi

echo "📦 Deployment: $ACTUAL_DEPLOYMENT_NAME"
echo "📂 Namespace: library-dev"

# Restart rollout to ensure fresh pods with latest image
kubectl rollout restart deployment/$ACTUAL_DEPLOYMENT_NAME -n library-dev

# Wait for rollout to complete
kubectl rollout status deployment/$ACTUAL_DEPLOYMENT_NAME -n library-dev --timeout=5m

echo "✅ Done! Your app should now be deployed to Minikube in namespace: library-dev"

# Checking pods
echo "🔍 Checking pods..."
kubectl get pods -n library-dev
