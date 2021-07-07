#!/bin/bash
VERSION_PROPERTY=$1
NEW_VERSION=$2
OLD_VERSION=$(./.github/sh/parse_gradle_property.sh "$VERSION_PROPERTY")
./.github/sh/validate_version_format.sh "$NEW_VERSION"
./.github/sh/validate_version_increased.sh "$OLD_VERSION" "$NEW_VERSION"
