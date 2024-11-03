#!/usr/bin/env bash

## This script compiles the project and runs all unit tests.

# The location of the maven settings file.
SETTINGS_FILE="${GITHUB_WORKSPACE}/settings.xml"

# The location of the maven pom file.
POM_FILE="${GITHUB_WORKSPACE}/pom.xml"

# Function that compiles and tests the code.
function compile_and_test() {
  local MVN_OPTS="--batch-mode --settings ${SETTINGS_FILE} --file ${POM_FILE}"
  mvn $MVN_OPTS test
}

compile_and_test