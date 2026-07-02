apiVersion: cert-manager.io/v1
kind: ClusterIssuer
metadata:
  name: selfsigned-bootstrap
spec:
  selfSigned: {}
---
apiVersion: cert-manager.io/v1
kind: Certificate
metadata:
  name: homelab-root-ca
  namespace: cert-manager
spec:
  isCA: true
  commonName: homelab-root-ca
  secretName: homelab-root-ca-secret
  duration: 87600h
  privateKey:
    algorithm: ECDSA
    size: 256
  issuerRef:
    name: selfsigned-bootstrap
    kind: ClusterIssuer
---
apiVersion: cert-manager.io/v1
kind: ClusterIssuer
metadata:
  name: homelab-ca-issuer
spec:
  ca:
    secretName: homelab-root-ca-secret
---
apiVersion: cert-manager.io/v1
kind: Certificate
metadata:
  name: local-cert
  namespace: ${CERT_NAMESPACE}
spec:
  secretName: local-cert-tls
  duration: 8760h
  renewBefore: 360h
  commonName: ${PRIMARY_DOMAIN}
  dnsNames:
    - ${PRIMARY_DOMAIN}
    - ${SECONDARY_DOMAIN}
  ipAddresses:
    - ${HOMELAB_IP}
  issuerRef:
    name: homelab-ca-issuer
    kind: ClusterIssuer