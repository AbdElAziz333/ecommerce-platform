# Gradle

gradle_build_discovery:
	./gradlew :discovery:bootRun --args='--spring.profiles.active=local' --console=plain
gradle_build_gateway:
	./gradlew :gateway:bootRun --args='--spring.profiles.active=local' --console=plain
gradle_build_product:
	./gradlew :product:bootRun --args='--spring.profiles.active=local' --console=plain
gradle_build_order:
	./gradlew :order:bootRun --args='--spring.profiles.active=local' --console=plain

# Run Locally

run_discovery_local:
	./gradlew :discovery:bootRun --args='--spring.profiles.active=local' --console=plain
run_product_local:
	./gradlew :product:bootRun --args='--spring.profiles.active=local' --console=plain
run_order_local:
	./gradlew :order:bootRun --args='--spring.profiles.active=local' --console=plain
run_gateway_local:
	./gradlew :gateway:bootRun --args='--spring.profiles.active=local' --console=plain

run_apps_local: run_discovery_local run_gateway_local run_product_local run_order_local

# npm

npm_run_local:
	npm run dev

# Docker Build

docker_build_discovery:
	docker compose -f backend/docker-compose.yaml build discovery

docker_build_product:
	docker compose -f backend/docker-compose.yaml build product

docker_build_order:
	docker compose -f backend/docker-compose.yaml build order

docker_build_gateway:
	docker compose -f backend/docker-compose.yaml build gateway

docker_build_all: docker_build_discovery docker_build_product docker_build_order docker_build_gateway
docker_build_k8s: docker_build_gateway docker_build_product docker_build_order

# Docker Run

docker_run_dbs_local:
	docker compose -f docker-compose.local.yaml --profile database up

docker_run_all_local:
	docker compose -f docker-compose.local.yaml --profile all up

docker_down_dbs_local:
	docker compose -f docker-compose.local.yaml --profile dbs down -v

docker_down_all_local:
	docker compose -f docker-compose.local.yaml --profile all down -v

# Docker Run

docker_run_apps:
	docker compose -f backend/docker-compose.yaml --profile app up

docker_run_dbs:
	docker compose -f backend/docker-compose.yaml --profile database up

docker_run_all:
	docker compose -f backend/docker-compose.yaml --profile all up

docker_apps_down:
	docker compose -f backend/docker-compose.yaml --profile app down -v

docker_dbs_down:
	docker compose -f backend/docker-compose.yaml --profile db down -v

docker_all_down:
	docker compose -f backend/docker-compose.yaml --profile all down -v

# Docker Push

docker_push_discovery:
	docker push abdelaziz333/ecommerce-discovery:0.0.1

docker_push_gateway:
	docker push abdelaziz333/ecommerce-gateway:0.0.1

docker_push_product:
	docker push abdelaziz333/ecommerce-product:0.0.1

docker_push_order:
	docker push abdelaziz333/ecommerce-order:0.0.1

docker_push_all: docker_push_discovery docker_push_gateway docker_push_product docker_push_order
docker_push_k8s: docker_push_gateway docker_push_product docker_push_order

# Kubernetes

kube_create_namespace:
	kubectl apply -f ./gitops/k8s/base/namespace.yml

kube_apply_postgres:
	kubectl apply -f ./gitops/k8s/base/postgres

kube_apply_redis:
	kubectl apply -f ./gitops/k8s/base/redis

kube_apply_dbs: kube_apply_postgres kube_apply_redis

kube_apply_gatway:
	kubectl apply -f ./gitops/k8s/base/gateway

kube_apply_product:
	kubectl apply -f ./gitops/k8s/base/product

kube_apply_order:
	kubectl apply -f ./gitops/k8s/base/order

kube_apply_kustomize:
	kubectl apply -k ./gitops/k8s/base

kube_apply_apps: kube_apply_gatway kube_apply_product kube_apply_order

kube_apply_all:
	kubectl apply -f ./gitops/k8s/base -R

kube_delete_all:
	kubectl delete -f ./gitops/k8s/base -R

kube_delete_kustomize:
	kubectl delete -k ./gitops/k8s/base/

flux_refresh:
	flux reconcile kustomization ecommerce-backend --with-source