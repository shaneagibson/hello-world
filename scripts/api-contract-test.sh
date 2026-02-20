#!/bin/bash

# API Contract Testing Script
# This script starts the application and runs Schemathesis contract tests

set -e

echo "Building application..."
./gradlew build

echo "Starting application..."
java -jar build/libs/hello-world-0.0.1-SNAPSHOT.jar &
APP_PID=$!

# Function to cleanup on exit
cleanup() {
    echo "Stopping application..."
    kill $APP_PID 2>/dev/null || true
    exit
}

trap cleanup EXIT INT TERM

echo "Waiting for application to start..."
timeout 60 bash -c 'until curl -f http://localhost:8080/api-docs 2>/dev/null; do sleep 2; done' || {
    echo "Application failed to start"
    exit 1
}

echo "Application started successfully!"
echo ""
echo "Running Schemathesis API contract tests..."
echo "=========================================="

if ! command -v schemathesis &> /dev/null; then
    echo "Schemathesis is not installed. Please install it:"
    echo "  pip install schemathesis"
    exit 1
fi

schemathesis run http://localhost:8080/api-docs \
    --checks all \
    --hypothesis-max-examples=100 \
    --hypothesis-deadline=5000 \
    --report \
    --verbose

echo ""
echo "=========================================="
echo "API contract tests completed!"
echo ""
echo "You can also view the OpenAPI spec at:"
echo "  http://localhost:8080/api-docs"
echo "  http://localhost:8080/swagger-ui.html"
