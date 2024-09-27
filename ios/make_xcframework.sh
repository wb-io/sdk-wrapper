pkgName=WBExchangeSdk
resultPackageDir=lib

backToRootPath="../../.."
cd ${pkgName}/Sources/${pkgName}

###################################################################

echo "------------------------------"
echo "MAKE ARCHIVE: $pkgName-iOS"
echo "------------------------------"

xcodebuild archive \
    -project $pkgName.xcodeproj \
    -scheme $pkgName \
    -destination "generic/platform=iOS" \
    -archivePath "$backToRootPath/$pkgName-iOS" \
    SKIP_INSTALL=NO \
    BUILD_LIBRARIES_FOR_DISTRIBUTION=YES

exitcode=$?
if [ $exitcode -eq 0 ] 
then 
    echo "ok..."
else 
    echo "-----------------"
    echo "ERROR = $exitcode"
    echo "-----------------"
    exit $exitcode
fi

###################################################################

echo "------------------------------"
echo "MAKE ARCHIVE: $pkgName-iOS_Simulator"
echo "------------------------------"

xcodebuild archive \
    -project $pkgName.xcodeproj \
    -scheme $pkgName \
    -destination "generic/platform=iOS Simulator" \
    -archivePath "$backToRootPath/$pkgName-iOS_Simulator" \
    SKIP_INSTALL=NO \
    BUILD_LIBRARIES_FOR_DISTRIBUTION=YES

exitcode=$?
if [ $exitcode -eq 0 ] 
then 
    echo "ok..."
else 
    echo "-----------------"
    echo "ERROR = $exitcode"
    echo "-----------------"
    exit $exitcode
fi

###################################################################

echo "------------------------------"
echo "MAKE XCF: $pkgName.xcframework"
echo "------------------------------"

cd $backToRootPath

xcodebuild -create-xcframework \
    -archive $pkgName-iOS.xcarchive -framework $pkgName.framework \
    -archive $pkgName-iOS_Simulator.xcarchive -framework $pkgName.framework \
    -output $pkgName.xcframework

exitcode=$?
if [ $exitcode -eq 0 ] 
then 
    echo "ok..."
else 
    echo "-----------------"
    echo "ERROR = $exitcode"
    echo "-----------------"
    exit $exitcode
fi

###################################################################

rm -rf $resultPackageDir/$pkgName/$pkgName.xcframework
mv $pkgName.xcframework $resultPackageDir/$pkgName

echo "------------------------------"
echo "REMOVE ARCHIVES..."

echo "REMOVE: $pkgName-iOS.xcarchive"
rm -rf $pkgName-iOS.xcarchive

echo "REMOVE: $pkgName-iOS_Simulator.xcarchive"
rm -rf $pkgName-iOS_Simulator.xcarchive
echo "------------------------------"
