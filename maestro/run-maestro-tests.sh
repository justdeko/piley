#!/bin/bash

required_tools=("emulator" "adb" "docker-compose" "maestro")

for tool in "${required_tools[@]}"; do
  if ! command -v "$tool" >/dev/null 2>&1; then
    echo "Error: $tool is not installed or not in the system's PATH."
    exit 1
  fi
done

# build debug apk
(cd .. && ./gradlew assembleDebug)

# start emulator and wait until online
emulator -avd Pixel_2_API_33 &
adb wait-for-device
echo "Emulator running"

# force install app
adb uninstall com.dk.piley
adb install -r ../app/build/outputs/apk/debug/app-debug.apk
echo "Installed apk"

# start docker desktop if on macos
if [[ "$OSTYPE" == "darwin"* ]]; then
  if command -v docker &>/dev/null; then
    open -a Docker
    echo "Docker app is starting..."
  else
    echo "Docker is not installed. Please install Docker and try again."
    exit 1
  fi
else
  echo "This script is intended for macOS only."
  exit 1
fi

(cd ../../piley-server && docker-compose up -d) # todo change piley-server directory after monorepo

echo "Starting tests..."
maestro test mainFlow.yaml
echo "Tests finished!..."

adb emu kill

(cd ../../piley-server && docker-compose down) # todo change piley-server directory after monorepo
