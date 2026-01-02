# Redis
k8s 접근 주소: my-redis-master.default.svc.cluster.local:6379

# helm 설치
brew install helm

# repo add & update
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update

# redis 설치 (namespace 생략)
helm install my-redis bitnami/redis

# Pod 확인
kubectl get pods
kubectl get pvc


