#!/usr/bin/env bash
set -euo pipefail

DIR="$(cd "$(dirname "$0")/.." && pwd)"

# Load .env if exists
if [ -f "$DIR/.env" ]; then
  set -a
  . "$DIR/.env"
  set +a
fi

# Ensure npm is available
if ! command -v npm >/dev/null 2>&1; then
  echo "npm not found. Please install Node.js >= 18." >&2
  exit 1
fi

# Install Node toolchain (local)
cd "$DIR"
npm install

# Verify Android tools
command -v adb >/dev/null 2>&1 || { echo "adb not found in PATH" >&2; exit 1; }
command -v avdmanager >/dev/null 2>&1 || echo "avdmanager not found (ok if you use existing AVDs)"

# Print versions
npx appium -v || true
adb version || true

echo "Setup completed. Use 'npx appium --base-path /wd/hub --port 4723' to start Appium."
