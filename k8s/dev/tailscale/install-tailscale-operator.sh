#!/usr/bin/env bash
set -euo pipefail
# ---------------------------------------------------------------------------
# Install the Tailscale Kubernetes Operator
#
# No oauth.clientId/oauth.clientSecret passed here at all -- the chart
# detects the pre-existing "operator-oauth" Secret (created by ESO from
# Vault in the previous step) and uses that instead.
#
# Run this AFTER 04-vault-secretstore-externalsecret.yaml has been applied
# and the Secret exists:
#   kubectl get secret operator-oauth -n rustfs
# ---------------------------------------------------------------------------

kubectl get secret operator-oauth -n rustfs >/dev/null 2>&1 || {
  echo "operator-oauth secret not found in the rustfs namespace yet."
  echo "Apply 04-vault-secretstore-externalsecret.yaml and wait for ESO to sync first."
  exit 1
}

helm repo add tailscale https://pkgs.tailscale.com/helmcharts
helm repo update

helm upgrade --install tailscale-operator tailscale/tailscale-operator \
  --namespace rustfs \
  --set-string operatorConfig.hostname="k3s-operator" \
  --wait

kubectl get pods -n rustfs