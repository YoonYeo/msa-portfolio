# k8s

## 로컬 맥 기준으로 작성되었으며, k8s는 로컬 컴퓨터의 도커 기반으로 구축하였습니다.

# kubectl 설치
brew install kubectl

# minikube 설치
brew install minikube

# minikube 시작
minikube start --driver=docker --memory=8192 --cpus=4

# 실행 확인
kubectl get nodes

# ingress Addon 활성화
minikube addons enable ingress
# 확인
kubectl get pods -n ingress-nginx

# Metrics Server 활성화
minikube addons enable metrics-server
# 확인
kubectl get pods -n kube-system | grep metrics



