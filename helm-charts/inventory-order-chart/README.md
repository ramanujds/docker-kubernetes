# inventory-order Helm chart

This chart deploys:

- `part-gateway` (Deployment + NodePort Service)
- `part-inventory-service` (Deployment + NodePort Service)
- `part-order-service` (Deployment + NodePort Service)

## Install

```sh
helm install inventory-order ./inventory-order-chart
```

## Upgrade

```sh
helm upgrade inventory-order ./inventory-order-chart
```

## Values you’ll likely change

- Images:
    - `partGateway.image.repository`, `partInventory.image.repository`, `partOrder.image.repository`
    - Optionally set `*.image.tag`
- Service types / nodePorts:
    - `partGateway.service.type` (NodePort/ClusterIP/LoadBalancer)
    - `partGateway.service.nodePort` (if NodePort)

## Notes

- Your apps reference a MySQL service named `mysql`. This chart assumes that service already exists (e.g., from a
  separate MySQL chart/manifests).

