#!/bin/bash

echo "=========================================="
echo "  Purchase Transaction API - Full Test"
echo "=========================================="
echo ""

# 1. Create purchase and capture ID
echo "Step 1: Creating Purchase..."
RESPONSE=$(curl -s --location 'http://localhost:8080/api/v1/purchases' \
--header 'Content-Type: application/json' \
--data '{
    "description": "Coffee purchase",
    "transactionDate": "2024-01-15",
    "purchaseAmount": 25.50
}')
echo "$RESPONSE"
echo ""

# Extract ID (requires jq)
if command -v jq &> /dev/null; then
    PURCHASE_ID=$(echo "$RESPONSE" | jq -r '.id')
    echo "Purchase ID: $PURCHASE_ID"
else
    echo "Note: Install jq to auto-extract purchase ID"
    PURCHASE_ID="d395cc92-f3f4-45e7-832a-1c275d4d703d"
fi
echo ""

# 2. Get purchase in USD
echo "Step 2: Getting Purchase (USD)..."
curl -s --location "http://localhost:8080/api/v1/purchases/${PURCHASE_ID}"
echo -e "\n"

# 3. Get purchase in EUR
echo "Step 3: Getting Purchase (EUR)..."
curl -s --location "http://localhost:8080/api/v1/purchases/${PURCHASE_ID}?currency=Euro%20Zone-Euro"
echo -e "\n"

# 4. Get purchase in BRL
echo "Step 4: Getting Purchase (BRL)..."
curl -s --location "http://localhost:8080/api/v1/purchases/${PURCHASE_ID}?currency=Brazil-Real"
echo -e "\n"

# 5. Health checks
echo "Step 5: Actuator Health..."
curl -s --location 'http://localhost:9090/actuator/health'
echo -e "\n"

echo "Step 6: FiscalData Health..."
curl -s --location 'http://localhost:9090/actuator/health/fiscalData'
echo -e "\n"

echo "=========================================="
echo "  All tests completed!"
echo "=========================================="
