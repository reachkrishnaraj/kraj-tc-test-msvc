#!/bin/bash
if [ "x$1" == "x" ]; then
  echo "Usage: $0 <new-msvc-prefix>"
  exit 1
fi
APP_PREFIX="$1"
APP_PASCAL_PREFIX=$(echo $APP_PREFIX | gsed -r 's/(^|-)([a-z])/\U\2/g')
APP_PACKAGE_PREFIX=$(echo $APP_PREFIX | gsed -r 's/.*/\L&/' | gsed -r 's/-//')
TEMPLATE_NAME='test-service-name'
TEMPLATE_PASCAL='testAppName'
TEMPLATE_PACKAGE='app.base.package.name'

echo "Will update these files $APP_PREFIX"
echo "======================="
grep -ri "${TEMPLATE_NAME}" * .idea .gitignore .teamcity
echo "======================="
echo "Updating"
grep -rli "${TEMPLATE_NAME}" * .idea .gitignore .teamcity | xargs -I@ sed -i "" "s/${TEMPLATE_NAME}/${APP_PREFIX}/g" @
echo "Updated files"
echo "======================="
grep -ri "${APP_PREFIX}" * .idea .gitignore .teamcity
echo "======================="

echo "Will update these files $APP_PASCAL_PREFIX"
echo "======================="
grep -ri "${TEMPLATE_PASCAL}" * .idea .teamcity
echo "======================="
echo "Updating"
grep -rli "${TEMPLATE_PASCAL}" * .idea .teamcity | xargs -I@ sed -i "" "s/${TEMPLATE_PASCAL}/${APP_PASCAL_PREFIX}/g" @
echo "Updated files"
echo "======================="
grep -ri "${APP_PASCAL_PREFIX}" * .idea .teamcity
echo "======================="

echo "Will update packages $TEMPLATE_PACKAGE to $APP_PACKAGE_PREFIX"
echo "======================="
grep -ri "${TEMPLATE_PACKAGE}" *
echo "======================="
echo "Updating"
grep -rli "${TEMPLATE_PACKAGE}" * | xargs -I@ sed -i "" "s/${TEMPLATE_PACKAGE}/${APP_PACKAGE_PREFIX}/g" @
echo "Updated files"
echo "======================="
grep -ri "${APP_PACKAGE_PREFIX}" *
echo "======================="

echo "update teamcity uuids"
echo "======================="
grep -ir 'uuid =' .
echo "======================="
sed -i "" "s/uuid = \"[-0-9A-Fa-f]*\"/uuid = \"$(uuidgen)\"/g" .teamcity/Microservices_testAppNameMsvc/Project.kt
sed -i "" "s/uuid = \"[-0-9A-Fa-f]*\"/uuid = \"$(uuidgen)\"/g" .teamcity/Microservices_testAppNameMsvc/buildTypes/Microservices_testAppNameMsvc_Build.kt
sed -i "" "s/uuid = \"[-0-9A-Fa-f]*\"/uuid = \"$(uuidgen)\"/g" .teamcity/Microservices_testAppNameMsvc/vcsRoots/Vcs_Microservices_testAppNameMsvc_testAppNameMsvc.kt
echo "======================="
grep -ir 'uuid =' .
echo "======================="


echo "renaming files"
echo "======================="
find . -iname "${TEMPLATE_NAME}*"
find . -iname "${TEMPLATE_PASCAL}*"
echo "======================="
mv .teamcity/Microservices_${TEMPLATE_PASCAL}Msvc/buildTypes/Microservices_${TEMPLATE_PASCAL}Msvc_Build.kt .teamcity/Microservices_${TEMPLATE_PASCAL}Msvc/buildTypes/Microservices_${APP_PASCAL_PREFIX}_Build.kt
mv .teamcity/Microservices_${TEMPLATE_PASCAL}Msvc/vcsRoots/Vcs_Microservices_${TEMPLATE_PASCAL}Msvc_${TEMPLATE_PASCAL}Msvc.kt .teamcity/Microservices_${TEMPLATE_PASCAL}Msvc/vcsRoots/Vcs_Microservices_${APP_PASCAL_PREFIX}Msvc_${APP_PASCAL_PREFIX}Msvc.kt
mv .teamcity/Microservices_${TEMPLATE_PASCAL}Msvc .teamcity/Microservices_${APP_PASCAL_PREFIX}Msvc
find . -iname "${TEMPLATE_NAME}*" -exec rename "s|${TEMPLATE_NAME}|${APP_PREFIX}|" {} +
find . -iname "${TEMPLATE_PASCAL}*" -exec rename "s|${TEMPLATE_PASCAL}|${APP_PASCAL_PREFIX}|" {} +
find . -iname "${TEMPLATE_PACKAGE}*" -exec rename "s|${TEMPLATE_PACKAGE}|${APP_PACKAGE_PREFIX}|" {} +
echo "======================="
echo "Renamed"
echo "======================="
find . -iname "${APP_PREFIX}*"
find . -iname "${APP_PASCAL_PREFIX}*"
echo "======================="




echo "Open with IntelliJ to recreate .idea module files"
