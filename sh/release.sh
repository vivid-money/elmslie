validateVersionFormat() {
  VERSION=$1
  EXPECTED='^([0-9]+){1,3}\.([0-9]+){1,9}\.([0-9]+){1,9}$'
  [[ ! $VERSION =~ $EXPECTED ]] && echo "Invalid version format: $VERSION" && exit 1
}

validateGithubTokenFormat() {
  EXPECTED='^[0-9a-z]{10,}$'
  [[ ! $TOKEN =~ $EXPECTED ]] && echo "Invalid github api token format: $TOKEN" && exit 1
}

parseProperty() {
  PROPERTY="$1"
  FILE="$2"
  sed -En "s/^$PROPERTY=([^\n]+)$/\1/p" $FILE
}

parseGithubToken() {
  parseProperty "github.api.token" "local.properties"
}

parseVersion() {
  parseProperty "libraryVersion" "gradle.properties"
}

updateVersionProperty() {
  VERSION=$1
  sed -E "s/^[#]*\s*libraryVersion=.*/libraryVersion=$VERSION/" gradle.properties >gradle.properties.tmp &&
    mv gradle.properties.tmp gradle.properties
}

executeTests() {
  ./gradlew test
}

checkCleanWorkingDirectory() {
  # Return 0 - empty, 1 - with file changes
  [[ ! -n $(git status -s) ]]
}

commitAndPush() {
  VERSION=$1
  git add -A
  git commit -m "Bump release version to $VERSION"
  git pull --rebase
  git push
}

createGithubRelease() {
  VERSION=$1
  curl \
    -X POST \
    -H "Authorization: token $TOKEN" \
    -H "Accept: application/vnd.github.v3+json" \
    https://api.github.com/repos/vivid-money/elmslie/releases \
    -d "{\"tag_name\": \"$VERSION\"}"
}

validateNewVersionIsGreater() {
  OLD_VERSION=$1
  NEW_VERSION=$2
  MIN_VERSION=$(printf "$OLD_VERSION\n$NEW_VERSION" | sort -V | head -n 1)
  [[ $OLD_VERSION == $NEW_VERSION ]] && echo "Can't update to the same version" && exit 1
  [[ $OLD_VERSION != $MIN_VERSION ]] &&
    echo "Can't update to an older version. Old: $OLD_VERSION. New: $NEW_VERSION" &&
    exit 1
}

ensureMainBranch() {
  CURRENT_BRANCH=$(git rev-parse --abbrev-ref HEAD)
  [[ $CURRENT_BRANCH != "main" ]] && echo "Can only release from 'main' branch" && exit 1
}

ensureMainBranch
checkCleanWorkingDirectory || { echo "Can't release not committed changes" && exit 1; }
OLD_VERSION=$(parseVersion)
echo "Starting to publish release: $OLD_VERSION"
NEW_VERSION=$1
validateVersionFormat $NEW_VERSION
validateNewVersionIsGreater $OLD_VERSION $NEW_VERSION

export TOKEN=$(parseGithubToken)
validateGithubTokenFormat

executeTests

createGithubRelease "$NEW_VERSION"
updateVersionProperty "$NEW_VERSION"
checkCleanWorkingDirectory && echo "Version was not updated" && exit 1
commitAndPush "$NEW_VERSION"
