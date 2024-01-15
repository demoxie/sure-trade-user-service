#!/bin/bash

SERVICE_NAME="user-service-dev"

# Check if the service is active
if sudo systemctl is-active --quiet $SERVICE_NAME ; then
    sudo systemctl restart $SERVICE_NAME
    echo "Service is running already. restarting it..."
else
    echo "Service is not running. Starting it..."
    sudo systemctl start $SERVICE_NAME
    echo $SERVICE_NAME." Service started."
fi
