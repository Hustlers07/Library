#!/bin/bash

# ─────────────────────────────────────────────
#  deploy.sh — Apply Kubernetes manifests with
#              ngrok auth token from env
# ─────────────────────────────────────────────

set -e  # exit on any error

# ── Colors ────────────────────────────────────
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# ── Config ────────────────────────────────────
NAMESPACE="library-dev"
NGROK_TUNNEL_FILE="ngrok.yml"
INGRESS_FILE="ingress.yaml"

# ── Helper functions ──────────────────────────
info()    { echo -e "${GREEN}[INFO]${NC}  $1"; }
warn()    { echo -e "${YELLOW}[WARN]${NC}  $1"; }
error()   { echo -e "${RED}[ERROR]${NC} $1"; exit 1; }

# ── Step 1: Check required tools ─────────────
info "Checking required tools..."

command -v kubectl   &>/dev/null || error "kubectl is not installed"
command -v envsubst  &>/dev/null || error "envsubst is not installed. Run: sudo apt install gettext-base"

info "All tools available ✓"

# ── Step 2: Ensure namespace exists ──────────
info "Ensuring namespace '$NAMESPACE' exists..."
kubectl get namespace "$NAMESPACE" &>/dev/null \
  || kubectl create namespace "$NAMESPACE"

# ── Step 3: Create/update ngrok secret ───────
info "Applying ngrok secret..."
kubectl create secret generic ngrok-credentials \
  --from-literal=authtoken="$NGROK_AUTHTOKEN" \
  --namespace="$NAMESPACE" \
  --dry-run=client -o yaml | kubectl apply -f -

info "Secret applied ✓"

# ── Step 4: Apply manifests ───────────────────

apply_if_exists() {
  local file=$1
  if [ -f "$file" ]; then
    info "Applying $file..."
    envsubst < "$file" | kubectl apply -f -
    info "$file applied ✓"
  else
    warn "$file not found, skipping."
  fi
}

apply_if_exists "$INGRESS_FILE"
apply_if_exists "$NGROK_TUNNEL_FILE"

# ── Step 5: Restart ngrok tunnel deployment ───
info "Restarting ngrok tunnel..."
kubectl rollout restart deployment/ngrok-tunnel -n "$NAMESPACE"
kubectl rollout status  deployment/ngrok-tunnel -n "$NAMESPACE" --timeout=60s

# ── Step 6: Verify secret value ───────────────
info "Verifying secret..."
STORED=$(kubectl get secret ngrok-credentials -n "$NAMESPACE" \
  -o jsonpath='{.data.authtoken}' | base64 --decode)

if [ "$STORED" == "$NGROK_AUTHTOKEN" ]; then
  info "Secret value verified ✓"
else
  error "Secret value mismatch! Re-run the script."
fi

# ── Step 7: Show pod status ───────────────────
echo ""
info "Current pods in '$NAMESPACE':"
kubectl get pods -n "$NAMESPACE"

echo ""
info "Fetching ngrok tunnel logs..."
kubectl logs -n "$NAMESPACE" deployment/ngrok-tunnel --tail=20

echo ""
info "✅ Deployment complete!"