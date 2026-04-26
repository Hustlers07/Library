#!/bin/bash

# Exit immediately if a command fails
set -e

# Switch Docker to use Minikube's environment
eval $(minikube docker-env)

# First argument is the image version
IMAGE_VERSION=$1

if [ -z "$IMAGE_VERSION" ]; then
  echo "Usage: ./deploy.sh <image-version>"
  exit 1
fi

echo "Deploying Spring Boot app with image version: $IMAGE_VERSION"

# Apply the Kubernetes manifest first (ensures deployment exists)
kubectl apply -f ./k8s/booking-management-deployment.yaml

# Update the image version in the deployment
kubectl set image deployment/booking-management booking-management=hellisback/library-booking-management-app:${IMAGE_VERSION} --record

# Wait for rollout to complete
kubectl rollout status deployment/booking-management

echo "Done! Your Spring Boot app should now be deployed to Minikube."

# Checking pods
echo "Checking pods..."
kubectl get pods