#!/bin/bash

echo "Enter API URL (default is emulator IP https://10.0.2.2/):"
read -r apiUrl

while [ -z "$apiUrl" ]; do
    echo "API URL cannot be empty. Please enter a valid API URL:"
    read -r apiUrl
done

echo "Building apk with url $apiUrl"

if ./gradlew assembleDebug -P API_URL="$apiUrl"; then
  echo "Build successful, apk located in app/build/outputs/apk/debug/app-debug.apk"
else
  echo "Build failed. Reason shown above"
fi
