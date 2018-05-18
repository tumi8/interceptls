#/bin/bash

VERSION=$1


if [ -z "${VERSION}" ]; then
    echo "USAGE: ./bump-version.sh <version-code>"
    exit 1
fi

echo "Setting the version to: $VERSION"
##
# java projects
##
mvn versions:set -DnewVersion=$VERSION

##
# android project
##

#set version number
# -i option is not cross plattform :(
sed "s/versionName .*/versionName \"$VERSION\"/" tls-client-android/app/build.gradle > tls-client-android/app/build.gradle.new
mv tls-client-android/app/build.gradle.new tls-client-android/app/build.gradle

#increment android version code
perl -i -pe 's/(versionCode )(\d+)$/$1.($2+1)/e' tls-client-android/app/build.gradle

