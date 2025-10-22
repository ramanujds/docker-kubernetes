### Core Components of Kubernetes RBAC:

1. **Role and ClusterRole**:
    - A **Role** contains permission rules within a *specific namespace*. It specifies what actions (verbs like get, list, create) can be performed on which resources (pods, deployments, services) inside that namespace.
    - A **ClusterRole** is similar, but it applies cluster-wide across all namespaces or on cluster-level resources like nodes.

2. **RoleBinding and ClusterRoleBinding**:
    - A **RoleBinding** ties a Role to users or groups within a given namespace, granting them those permissions for that namespace.
    - A **ClusterRoleBinding** connects a ClusterRole to users or groups across the entire cluster.

3. **Subjects**: The users, groups, or service accounts that the permissions are assigned to via bindings.

### How RBAC Works

- You define Roles/ClusterRoles containing rules specifying allowed actions like “get,” “list,” or “create” on Kubernetes resources. These permissions are additive.
- Then, you create bindings that connect these roles to particular users or accounts. This way, users are only allowed the actions their assigned role permits.
- RBAC is enforced by the Kubernetes API server for all API requests.

### Example Role and RoleBinding

```yaml
apiVersion: rbac.authorization.k8s.io/v1
kind: Role
metadata:
  namespace: default
  name: pod-reader
rules:
- apiGroups: [""]
  resources: ["pods"]
  verbs: ["get", "watch", "list"]
---
apiVersion: rbac.authorization.k8s.io/v1
kind: RoleBinding
metadata:
  name: read-pods
  namespace: default
subjects:
- kind: User
  name: jane
  apiGroup: rbac.authorization.k8s.io
roleRef:
  kind: Role
  name: pod-reader
  apiGroup: rbac.authorization.k8s.io
```

In the above, user "jane" is granted permission to read pods in the "default" namespace. This gives her access limited strictly to reading pods but nothing else in that namespace.

### Use Cases for RBAC in Kubernetes

- Enforce strict access control for multi-tenant clusters where different teams manage different namespaces.
- Allow developers permission to manage pods but restrict admin permissions like deleting namespaces or modifying nodes.
- Limit access of service accounts used by different tools or controllers to only the resources they need.
- Secure a Kubernetes cluster by preventing unauthorized or accidental harmful actions.

### Default Roles in Kubernetes

Kubernetes provides a few default ClusterRoles such as:

- **cluster-admin**: Unrestricted full control across the cluster.
- **admin**: Broad read/write within a namespace, including role management.
- **edit**: Can create and update most objects in a namespace but not manage roles.
- **view**: Read-only access in a namespace.

RBAC can be customized by creating additional Roles and bindings tailored to an organization's needs.

***

In summary, Kubernetes RBAC controls *who* can do *what* to *which resources* within the cluster. It offers fine-grained access control through roles and bindings to enforce least privilege and secure cluster operations effectively.[1][2][4][5][9]

