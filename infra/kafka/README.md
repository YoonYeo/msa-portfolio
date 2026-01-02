# kafka

## strimzi 0.49, kafka V 4.1.1

# 카프카 설치
kubectl create namespace kafka
kubectl apply -n kafka -f "https://strimzi.io/install/latest?namespace=kafka"

# 설치 확인
### 1~2분 정도 컨테이너 초기화 작업 있음
kubectl get pods -n kafka

# kafka 클러스터 설정
kubectl apply -f kafka-cluster.yaml
# KRaft 설정
kubectl apply -f kafka-nodepool.yaml

# minikube ip 확인
minikube ip
# 클러스터 노드 포트 확인
kubectl get svc my-kafka-kafka-external-bootstrap -n kafka

# 로컬 접속 주소
192.168.49.2:32309

# k8s 내부 접속 주소
my-kafka-kafka-bootstrap.kafka.svc:9092





