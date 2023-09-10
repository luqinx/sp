#!/bin/sh

#./gradlew buildDebug -xlint -xtest --stacktrace -Dorg.gradle.debug=true
./gradlew clean && ./gradlew :app:assembleDebug -xlint -xtest --stacktrace -Dorg.gradle.daemon=false -Dorg.gradle.debug=true
#./gradlew clean && ./gradlew :app:assembleDebug  -Dorg.gradle.debug=true
