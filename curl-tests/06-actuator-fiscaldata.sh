#!/bin/bash

echo "=== Actuator FiscalData Health ==="
curl --location 'http://localhost:9090/actuator/health/fiscalData'
echo -e "\n"
