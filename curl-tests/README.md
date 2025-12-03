# Curl Tests - Purchase Transaction API

Test scripts for the Purchase Transaction API.

## Structure

- `01-create-purchase.sh` - Create a new purchase
- `02-get-purchase.sh` - Get purchase in USD
- `03-get-purchase-eur.sh` - Get purchase converted to EUR
- `04-get-purchase-brl.sh` - Get purchase converted to BRL
- `05-actuator-health.sh` - Check general health
- `06-actuator-fiscaldata.sh` - Check FiscalData health
- `07-validation-errors.sh` - Test error cases
- `run-all-tests.sh` - Run all tests in sequence

## How to Use

### Grant execution permission

```bash
chmod +x curl-tests/*.sh
```

### Run all tests

```bash
./curl-tests/run-all-tests.sh
```

### Run individual test

```bash
# Create purchase
./curl-tests/01-create-purchase.sh

# Get purchase (replace the ID)
./curl-tests/02-get-purchase.sh d395cc92-f3f4-45e7-832a-1c275d4d703d

# Test validations
./curl-tests/07-validation-errors.sh
```

## Endpoints

### Main API (port 8080)

- POST `/api/v1/purchases` - Create purchase
- GET `/api/v1/purchases/{id}` - Get purchase
- GET `/api/v1/purchases/{id}?currency={currency}` - Get with conversion

### Actuator (port 9090)

- GET `/actuator/health` - General health
- GET `/actuator/health/fiscalData` - FiscalData health
- GET `/actuator/info` - Application info
- GET `/actuator/metrics` - Metrics

## Supported Currencies

- `United States-Dollar` (default)
- `Euro Zone-Euro`
- `Brazil-Real`
- Other currencies available in Treasury API

## Notes

- The `run-all-tests.sh` script requires `jq` to automatically extract the purchase ID
- Install jq: `brew install jq` (macOS) or `apt-get install jq` (Linux)
- Without jq, use the default ID or manually replace it in the scripts
