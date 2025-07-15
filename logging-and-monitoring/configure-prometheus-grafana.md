
# Steps to Configure Prometheus and Grafana on Kubernetes

## 1. Add Helm Repositories

```sh
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo add grafana https://grafana.github.io/helm-charts
helm repo update
```

## 2. Install Prometheus

```sh
helm install prometheus prometheus-community/prometheus --version 27.8.0
```

## 3. Install Grafana

```sh
helm install grafana grafana/grafana --version 8.12.0
```

## 4. Expose Services

```sh
kubectl expose service grafana --type=NodePort --target-port=3000 --name=grafana-ext
kubectl expose service prometheus-server --type=NodePort --target-port=9090 --name=prometheus-server-ext
```

## 5. Access Services via Minikube

```sh
minikube service grafana-ext --url
minikube service prometheus-server-ext --url
```

Copy the URL for `prometheus-server-ext` (the first one shown).

## 6. Configure Grafana

1. Open Grafana in your browser using the URL from above.
2. Add a new data source:
    - Select **Prometheus** as the data source type.
    - Paste the `prometheus-server-ext` URL.
3. Create a new dashboard:
    - Go to **Create** → **Import Dashboard**.
    - Input dashboard ID: `15661`.
    - Set the data source to **Prometheus**.
