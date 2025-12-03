#!/bin/bash

echo "=== Creating Purchase ==="
curl --location 'http://localhost:8080/api/v1/purchases' \
--header 'Content-Type: application/json' \
--data '{
    "description": "Coffee purchase",
    "transactionDate": "2024-01-15",
    "purchaseAmount": 25.50
}'
echo -e "\n"
