#!/usr/bin/env bash

## This script deploys a maven jar to a repository.

# The location of the maven settings file.
SETTINGS_FILE="${GITHUB_WORKSPACE}/settings.xml"

# The location of the maven pom file.
POM_FILE="${GITHUB_WORKSPACE}/pom.xml"

# The server id (for credentials) :: url to the repository.
MAVEN_REPO="nepherte-releases::https://mvn.nepherte.com/releases"

# Function that deploys the maven archetype.
function deploy_artifact() {
  local MVN_OPTS="--batch-mode --settings ${SETTINGS_FILE} --file ${POM_FILE}"
  local GOAL_PROPS="-DskipTests -DaltDeploymentRepository=$MAVEN_REPO"
  mvn $MVN_OPTS deploy $GOAL_PROPS
}

deploy_artifact