#!/bin/bash

# Substitua o ID pelo retornado na criação
PURCHASE_ID="${1:-d395cc92-f3f4-45e7-832a-1c275d4d703d}"

echo "=== Getting Purchase (BRL) ==="
curl --location "http://localhost:8080/api/v1/purchases/${PURCHASE_ID}?currency=Brazil-Real"
echo -e "\n"
