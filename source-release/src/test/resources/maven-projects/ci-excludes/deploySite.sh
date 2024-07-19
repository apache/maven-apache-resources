#!/bin/sh

mvn -Preporting site site:stage "$@"
mvn scm-publish:publish-scm "$@"
