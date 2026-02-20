#!/bin/bash

# Script to install Git pre-commit hooks

HOOKS_DIR="hooks"
GIT_HOOKS_DIR=".git/hooks"

# Check if we're in a git repository
if [ ! -d ".git" ]; then
    echo "❌ Error: Not a git repository. Please run 'git init' first."
    exit 1
fi

# Check if hooks directory exists
if [ ! -d "$HOOKS_DIR" ]; then
    echo "❌ Error: hooks directory not found."
    exit 1
fi

# Create .git/hooks directory if it doesn't exist
mkdir -p "$GIT_HOOKS_DIR"

# Copy pre-commit hook
if [ -f "$HOOKS_DIR/pre-commit" ]; then
    cp "$HOOKS_DIR/pre-commit" "$GIT_HOOKS_DIR/pre-commit"
    chmod +x "$GIT_HOOKS_DIR/pre-commit"
    echo "✅ Pre-commit hook installed successfully!"
else
    echo "❌ Error: pre-commit hook not found in $HOOKS_DIR"
    exit 1
fi

echo ""
echo "Git hooks have been installed."
echo "The pre-commit hook will run security checks, SAST, and linting before each commit."
echo ""
echo "Secret Scanning (Gitleaks):"
echo "  Install gitleaks: brew install gitleaks (macOS)"
echo "  Manual scan:      gitleaks protect --staged --verbose"
echo ""
echo "Code Linting (ktlint):"
echo "  Check formatting: ./gradlew ktlintCheck"
echo "  Fix formatting:   ./gradlew ktlintFormat"
echo ""
echo "SAST (SpotBugs + FindSecBugs):"
echo "  Run analysis:     ./gradlew spotbugsMain spotbugsTest"
echo "  View reports:     build/reports/spotbugs/main/spotbugs.html"
