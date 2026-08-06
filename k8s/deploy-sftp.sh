#!/usr/bin/env bash
set -euo pipefail

# Deploy SFTP using envsubst to replace env variables in the manifest.
# Expects SFTP_USERNAME and SFTP_PASSWORD to be exported by the caller:
#
#   export SFTP_USERNAME=myuser
#   export SFTP_PASSWORD=mypassword
#   ./deploy-sftp.sh

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MANIFEST="$DIR/dev/sftp.yml"
NAMESPACE="library-dev"

# ── Required variables ────────────────────────────────────────────────────────
: "${SFTP_USERNAME:?Please export SFTP_USERNAME before running this script}"
: "${SFTP_PASSWORD:?Please export SFTP_PASSWORD before running this script}"

# ── Optional variables (with sane defaults) ───────────────────────────────────
SFTP_UID="${SFTP_UID:-1001}"
SFTP_GID="${SFTP_GID:-100}"

export SFTP_USERNAME SFTP_PASSWORD SFTP_UID SFTP_GID

# ── Pre-flight checks ─────────────────────────────────────────────────────────
if ! command -v envsubst >/dev/null; then
  echo "envsubst required but not found. Install gettext (provides envsubst)." >&2
  exit 1
fi

if ! command -v kubectl >/dev/null; then
  echo "kubectl required but not found." >&2
  exit 1
fi

if [ ! -f "$MANIFEST" ]; then
  echo "Manifest not found: $MANIFEST" >&2
  exit 1
fi

# ── SSH host keys (generated once; reused across redeployments) ───────────────
HOST_KEYS_DIR="/home/groot/Documents/rustfs/sftp-host-keys"
mkdir -p "$HOST_KEYS_DIR"

if [ ! -f "$HOST_KEYS_DIR/ssh_host_ed25519_key" ]; then
  echo "Generating ED25519 host key..."
  ssh-keygen -t ed25519 -f "$HOST_KEYS_DIR/ssh_host_ed25519_key" -N ""
fi

if [ ! -f "$HOST_KEYS_DIR/ssh_host_rsa_key" ]; then
  echo "Generating RSA host key..."
  ssh-keygen -t rsa -b 4096 -f "$HOST_KEYS_DIR/ssh_host_rsa_key" -N ""
fi

# sshd requires private keys to be owned by root and not group/world readable.
# If running as non-root, sudo is needed.
chmod 600 "$HOST_KEYS_DIR/ssh_host_ed25519_key" "$HOST_KEYS_DIR/ssh_host_rsa_key"
chmod 644 "$HOST_KEYS_DIR/ssh_host_ed25519_key.pub" "$HOST_KEYS_DIR/ssh_host_rsa_key.pub"

echo "Host keys ready at $HOST_KEYS_DIR"

# ── Apply ─────────────────────────────────────────────────────────────────────
echo "Rendering $MANIFEST with env vars and applying to namespace '$NAMESPACE'..."
envsubst < "$MANIFEST" | kubectl apply -f -

echo "Restarting deployment/sftp to pick up any changes..."
kubectl rollout restart deployment/sftp -n "$NAMESPACE"
kubectl rollout status  deployment/sftp -n "$NAMESPACE"

echo ""
echo "Done."
echo "  Internal (ClusterIP) : sftp-service.$NAMESPACE.svc.cluster.local:22"
echo "  External (NodePort)  : <node-ip>:30222"
echo "  Tail logs            : kubectl logs -f deploy/sftp -n $NAMESPACE"