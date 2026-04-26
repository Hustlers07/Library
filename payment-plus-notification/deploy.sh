#!/bin/bash

# Exit immediately if a command fails
set -e

# Switch Docker to use Minikube's environment
eval $(minikube docker-env)

# First argument is the image version
# IMAGE_VERSION=$1

# if [ -z "$IMAGE_VERSION" ]; then
#   echo "Usage: ./deploy.sh <image-version>"
#   exit 1
# fi

# echo "Deploying Spring Boot app with image version: $IMAGE_VERSION"

# Apply the Kubernetes manifest first (ensures deployment exists)
kubectl apply -f ./k8s/payment-plus-notification-deployment.yaml

# Update the image version in the deployment
# kubectl set image deployment/payment-plus-notification payment-plus-notification=hellisback/library-payment-plus-notification-app:${IMAGE_VERSION} --record

kubectl set image deployment/payment-plus-notification payment-plus-notification=hellisback/library-payment-plus-notification-app:latest --record

# Wait for rollout to complete
kubectl rollout status deployment/payment-plus-notification

echo "Done! Your Spring Boot app should now be deployed to Minikube."

# Checking pods
echo "Checking pods..."
kubectl get pods