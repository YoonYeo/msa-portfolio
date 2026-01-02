# argoCD

# namespace 생성
kubectl create namespace argocd

# 설치
kubectl apply -n argocd \
-f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

