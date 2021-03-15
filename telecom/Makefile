.PHONY: port-forward-prod
port-forward-prod:
  kubectl port-forward -n production deployment/cipher 9999:18081 &
	kubectl port-forward -n production deployment/collectmydata-connect 9998:8080 &

.PHONY: port-forward-stg
port-forward-stg:
  kubectl port-forward -n staging deployment/cipher 9999:18081 &
	kubectl port-forward -n staging deployment/collectmydata-connect 9998:8080 &

.PHONY: kill-port-forward
kill-port-forward:
	./../kill-port-forward.sh
