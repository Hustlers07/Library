#!/bin/bash

# Exit immediately if a command fails
set -e

# Check if Minikube is running
if ! minikube status | grep -q "Running"; then
  echo "Error: Minikube is not running."
  echo "Please start Minikube with: minikube start"
  exit 1
fi

# Switch Docker to use Minikube's environment
eval $(minikube docker-env)

# First argument is the deployment name
DEPLOYMENT_NAME=$1

# Check if deployment name is provided
if [ -z "$DEPLOYMENT_NAME" ]; then
  echo "Error: Deployment name not provided."
  echo "Usage: ./deploy.sh <deployment-name>"
  exit 1
fi

echo "Deploying app to deployment: $DEPLOYMENT_NAME"

# Create namespace first (idempotent operation)
kubectl apply -f ./dev/namespace.yaml

# Apply ConfigMaps and Secrets if they exist
[ -f ./dev/configmap.yaml ] && kubectl apply -f ./dev/configmap.yaml
[ -f ./dev/secrets.yaml ] && kubectl apply -f ./dev/secrets.yaml

# Apply the Kubernetes deployment manifest
if [ ! -f "./dev/${DEPLOYMENT_NAME}-deployment.yaml" ]; then
  echo "Error: Deployment manifest not found at ./dev/${DEPLOYMENT_NAME}-deployment.yaml"
  exit 1
fi

kubectl apply -f ./dev/${DEPLOYMENT_NAME}-deployment.yaml

# Extract the actual deployment name from the manifest
ACTUAL_DEPLOYMENT_NAME=$(grep "^  name:" ./dev/${DEPLOYMENT_NAME}-deployment.yaml | head -1 | awk '{print $2}')

if [ -z "$ACTUAL_DEPLOYMENT_NAME" ]; then
  echo "Error: Could not extract deployment name from manifest"
  exit 1
fi

# Wait for rollout to complete with timeout
kubectl rollout status deployment/$ACTUAL_DEPLOYMENT_NAME -n library-dev --timeout=5m

echo "Done! Your Spring Boot app should now be deployed to Minikube."

# Checking pods
echo "Checking pods..."
kubectl get pods -n library-dev