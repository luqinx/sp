#!/bin/sh
./gradlew buildDebug -xlint -xtest --stacktrace -Dorg.gradle.debug=true 
