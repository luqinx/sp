#!/bin/sh
#./gradlew buildDebug -xlint -xtest --stacktrace -Dorg.gradle.debug=true
./gradlew :app:assembleDebug -xlint -xtest --stacktrace -Dorg.gradle.debug=true
