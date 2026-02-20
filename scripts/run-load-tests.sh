#!/bin/bash

# Load Testing Script for Hello World Application
# This script starts the application and runs Gatling load tests

set -e

echo "======================================"
echo "Hello World Load Testing"
echo "======================================"
echo ""

# Check if application is already running
if curl -f http://localhost:8080/actuator/health 2>/dev/null; then
    echo "✅ Application is already running"
    RUN_APP=false
else
    echo "Starting application..."
    RUN_APP=true

    # Build the application
    ./gradlew build -x test

    # Start the application in the background
    java -jar build/libs/hello-world-0.0.1-SNAPSHOT.jar &
    APP_PID=$!

    # Function to cleanup on exit
    cleanup() {
        if [ "$RUN_APP" = true ]; then
            echo ""
            echo "Stopping application..."
            kill $APP_PID 2>/dev/null || true
        fi
        exit
    }

    trap cleanup EXIT INT TERM

    # Wait for application to start
    echo "Waiting for application to start..."
    timeout 60 bash -c 'until curl -f http://localhost:8080/actuator/health 2>/dev/null; do sleep 2; done' || {
        echo "❌ Application failed to start"
        exit 1
    }

    echo "✅ Application started successfully"
fi

echo ""
echo "======================================"
echo "Running Gatling Load Tests"
echo "======================================"
echo ""

# Get test type from argument or default to load test
TEST_TYPE=${1:-load}

case $TEST_TYPE in
    load)
        echo "Running Load Test (simulates normal traffic)..."
        ./gradlew gatlingRun --simulation=com.example.helloworld.simulations.HelloWorldLoadTest
        ;;
    stress)
        echo "Running Stress Test (finds breaking point)..."
        ./gradlew gatlingRun --simulation=com.example.helloworld.simulations.HelloWorldStressTest
        ;;
    all)
        echo "Running all test scenarios..."
        ./gradlew gatlingRun
        ;;
    *)
        echo "Unknown test type: $TEST_TYPE"
        echo "Usage: $0 [load|stress|all]"
        exit 1
        ;;
esac

echo ""
echo "======================================"
echo "Load tests completed!"
echo "======================================"
echo ""
echo "📊 View reports at: build/reports/gatling/"
echo ""

# Open report in browser (macOS)
if [ "$(uname)" = "Darwin" ]; then
    LATEST_REPORT=$(ls -t build/reports/gatling/*/index.html 2>/dev/null | head -1)
    if [ -n "$LATEST_REPORT" ]; then
        echo "Opening report in browser..."
        open "$LATEST_REPORT"
    fi
fi
