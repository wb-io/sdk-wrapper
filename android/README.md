# White Bird Exchange SDK for Android - Developer Guide

## Setup Requirements

### Android Studio Installation

For installation tips from JetBrains, visit:
[https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-setup.html](https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-setup.html)

### GitHub Repository

The project repository is located at: https://github.com/wb-io/sdk-wrapper

## Project Structure

The project contains several modules:

- **wbexchangesdk** - The SDK module
- **example** - Application for SDK development and testing
- **example_with_aar** - Example application using the SDK as an AAR file

You can run any module using Android Studio, for example, the `example` module.

## Building the AAR Module

You can build the AAR module in two ways:

1. **From Android Studio menu**:
    - The AAR file will be created at `build/outputs/aar/`
    - Note: This method may only create the debug version

2. **From command line** (recommended):
   ```
   ./gradlew build -p wbexchangesdk
   ```
    - This creates both debug and release versions

**Debug version** - allows loading from local server via HTTP  
**Release version** - only allows HTTPS connections

After building, rename the file to match your version, e.g., `wbexchangesdk-0.2.0.aar`

## Environment Configuration

The SDK supports three environments:
- DEV - Development environment
- QA - Testing environment
- PROD - Production environment

The environment is specified during SDK initialization:
```kotlin
wbExchangeSdk.setup(
    environment = WBExchangeEnvironment.DEV,
    // Other parameters...
)
```

## Testing with Example Projects

### Example Project

The `example` project demonstrates how to use the SDK as a direct module dependency. This is useful during development to immediately see changes made to the SDK code.

### Example with AAR Project

The `example_with_aar` project demonstrates how to use the SDK as a pre-built AAR file. This simulates how third-party developers will integrate the SDK.

To test with this project:
1. Build the SDK as described above
2. Copy the AAR file to `example_with_aar/libs/wbexchangesdk-0.2.0.aar`
3. Run the `example_with_aar` project

## Minimum Platform Version

The minimum Android platform version for the SDK is API 24 (Android 7.0).

For more information on Android platform versions, see:
[https://developer.android.com/tools/releases/platforms](https://developer.android.com/tools/releases/platforms) 
