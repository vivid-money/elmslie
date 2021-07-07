#!/bin/bash
KEY=$1
VALUE=$2
./.github/sh/update_gradle_property.sh "$KEY" "$VALUE"
./.github/sh/validate_has_git_changes.sh
