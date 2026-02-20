# Hello World Helm Chart

This Helm chart deploys the Hello World Spring Boot application to Kubernetes.

## Prerequisites

- Kubernetes 1.19+
- Helm 3.2.0+
- PV provisioner support in the underlying infrastructure (for persistent volumes)

## Installing the Chart

To install the chart with the release name `my-hello-world`:

```bash
helm install my-hello-world ./helm/hello-world
```

The command deploys Hello World on the Kubernetes cluster with default configuration. The [Parameters](#parameters) section lists the parameters that can be configured during installation.

## Uninstalling the Chart

To uninstall/delete the `my-hello-world` deployment:

```bash
helm uninstall my-hello-world
```

## Parameters

### Global parameters

| Name                      | Description                                     | Value               |
| ------------------------- | ----------------------------------------------- | ------------------- |
| `replicaCount`            | Number of replicas                              | `2`                 |
| `image.repository`        | Image repository                                | `hello-world`       |
| `image.pullPolicy`        | Image pull policy                               | `IfNotPresent`      |
| `image.tag`               | Image tag (defaults to chart appVersion)        | `""`                |

### Service parameters

| Name                      | Description                                     | Value               |
| ------------------------- | ----------------------------------------------- | ------------------- |
| `service.type`            | Kubernetes Service type                         | `ClusterIP`         |
| `service.port`            | Service port                                    | `80`                |
| `service.targetPort`      | Container port                                  | `8080`              |

### Ingress parameters

| Name                      | Description                                     | Value               |
| ------------------------- | ----------------------------------------------- | ------------------- |
| `ingress.enabled`         | Enable ingress controller resource              | `false`             |
| `ingress.className`       | Ingress class name                              | `nginx`             |
| `ingress.hosts[0].host`   | Hostname                                        | `hello-world.example.com` |

### Resource limits and requests

| Name                      | Description                                     | Value               |
| ------------------------- | ----------------------------------------------- | ------------------- |
| `resources.limits.cpu`    | CPU limit                                       | `500m`              |
| `resources.limits.memory` | Memory limit                                    | `512Mi`             |
| `resources.requests.cpu`  | CPU request                                     | `250m`              |
| `resources.requests.memory` | Memory request                                | `256Mi`             |

### Autoscaling parameters

| Name                      | Description                                     | Value               |
| ------------------------- | ----------------------------------------------- | ------------------- |
| `autoscaling.enabled`     | Enable Horizontal Pod Autoscaler                | `false`             |
| `autoscaling.minReplicas` | Minimum number of replicas                      | `2`                 |
| `autoscaling.maxReplicas` | Maximum number of replicas                      | `10`                |

### Security parameters

| Name                      | Description                                     | Value               |
| ------------------------- | ----------------------------------------------- | ------------------- |
| `podSecurityContext.runAsNonRoot` | Run container as non-root user          | `true`              |
| `podSecurityContext.runAsUser`    | User ID to run the container            | `1000`              |
| `securityContext.allowPrivilegeEscalation` | Allow privilege escalation   | `false`             |

## Configuration and installation details

### Resource Limits

The default configuration includes resource limits and requests. Adjust these based on your cluster capacity and application needs.

### High Availability

For production deployments:
- Enable autoscaling
- Set `replicaCount` to at least 2
- Enable `podDisruptionBudget`
- Configure anti-affinity rules (enabled by default)

### Monitoring

To enable Prometheus monitoring:

```yaml
serviceMonitor:
  enabled: true
  interval: 30s
```

### Security

The chart follows security best practices:
- Non-root user execution
- Read-only root filesystem
- Dropped capabilities
- Security context constraints

## Examples

### Install with custom values

```bash
helm install my-hello-world ./helm/hello-world \
  --set replicaCount=3 \
  --set ingress.enabled=true \
  --set ingress.hosts[0].host=myapp.example.com
```

### Install with values file

Create a `values-production.yaml`:

```yaml
replicaCount: 3

ingress:
  enabled: true
  hosts:
    - host: myapp.example.com
      paths:
        - path: /
          pathType: Prefix

autoscaling:
  enabled: true
  minReplicas: 3
  maxReplicas: 10

resources:
  limits:
    cpu: 1000m
    memory: 1Gi
  requests:
    cpu: 500m
    memory: 512Mi
```

Install with:

```bash
helm install my-hello-world ./helm/hello-world -f values-production.yaml
```

## Upgrading

To upgrade the release:

```bash
helm upgrade my-hello-world ./helm/hello-world
```

## License

This Helm chart is provided as-is for educational purposes.
