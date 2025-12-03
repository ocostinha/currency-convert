#!/bin/bash

echo "=== Actuator Health ==="
curl --location 'http://localhost:9090/actuator/health'
echo -e "\n"
