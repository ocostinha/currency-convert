#!/bin/bash

echo "=== Test: Empty Description ==="
curl --location 'http://localhost:8080/api/v1/purchases' \
--header 'Content-Type: application/json' \
--data '{
    "description": "",
    "transactionDate": "2024-01-15",
    "purchaseAmount": 25.50
}'
echo -e "\n\n"

echo "=== Test: Description Too Long ==="
curl --location 'http://localhost:8080/api/v1/purchases' \
--header 'Content-Type: application/json' \
--data '{
    "description": "This is a very long description that exceeds fifty characters limit",
    "transactionDate": "2024-01-15",
    "purchaseAmount": 25.50
}'
echo -e "\n\n"

echo "=== Test: Negative Amount ==="
curl --location 'http://localhost:8080/api/v1/purchases' \
--header 'Content-Type: application/json' \
--data '{
    "description": "Invalid purchase",
    "transactionDate": "2024-01-15",
    "purchaseAmount": -10.00
}'
echo -e "\n\n"

echo "=== Test: Purchase Not Found ==="
curl --location 'http://localhost:8080/api/v1/purchases/00000000-0000-0000-0000-000000000000'
echo -e "\n\n"

echo "=== Test: Invalid Currency ==="
curl --location 'http://localhost:8080/api/v1/purchases/d395cc92-f3f4-45e7-832a-1c275d4d703d?currency=INVALID'
echo -e "\n"
