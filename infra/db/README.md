# db

# 네임스페이스 생성
kubectl create ns db
# DB 비밀번호 Secret
kubectl create secret generic mariadb-secret -n db --from-literal=root-password=root1234

# statefulset, service 적용
kubectl apply -f ./mariadb-statefulset.yaml
kubectl apply -f ./mariadb-service.yaml

# bash 접근
kubectl exec -it -n db mariadb-0 -- mysql -uroot -p

# DB 생성
CREATE DATABASE auth_db;
CREATE DATABASE order_db;

CREATE USER 'auth_user'@'%' IDENTIFIED BY 'auth1234';
CREATE USER 'order_user'@'%' IDENTIFIED BY 'order1234';

GRANT ALL PRIVILEGES ON auth_db.* to 'auth_user'@'%';
GRANT ALL PRIVILEGES ON order_db.* to 'order_user'@'%';

FLUSH PRIVILEGES;

# database URL
jdbc:mariadb://mariadb.db.svc.cluster.local:3306/db_schema