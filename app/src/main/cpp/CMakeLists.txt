# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html.
# For more examples on how to use CMake, see https://github.com/android/ndk-samples.

# Sets the minimum CMake version required for this project.
cmake_minimum_required(VERSION 3.22.1)

# Declares the project name. The project name can be accessed via ${ PROJECT_NAME},
# Since this is the top level CMakeLists.txt, the project name is also accessible
# with ${CMAKE_PROJECT_NAME} (both CMake variables are in-sync within the top level
# build script scope).
project("nativesqlite")


# Declares and names the project.

set(NATIVE_LIB "nativesqlite")

add_library(crypto STATIC IMPORTED)
set_target_properties(crypto PROPERTIES IMPORTED_LOCATION ${CMAKE_HOME_DIRECTORY}/boringssl/lib/libcrypto_${ANDROID_ABI}.a)

add_library(ssl STATIC IMPORTED)
set_target_properties(ssl PROPERTIES IMPORTED_LOCATION ${CMAKE_HOME_DIRECTORY}/boringssl/lib/libssl_${ANDROID_ABI}.a)
# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.


add_library( # Sets the name of the library.
        ${NATIVE_LIB}
        # Sets the library as a shared library.
        SHARED
        #        SqliteWrapper.cpp
        SqliteWrapper.cpp
        TgNetWrapper.cpp
        # Provides a relative path to your source file(s).
        native-lib.cpp)

add_library(tgnet STATIC
        tgnet/ConnectionsManager.cpp
        tgnet/BuffersStorage.cpp
        tgnet/NativeByteBuffer.cpp
        tgnet/ByteArray.cpp
        )
target_include_directories(tgnet PUBLIC
        boringssl/include/)
target_link_libraries(tgnet
        crypto)
# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)
add_library(sqlite STATIC
        sqlite/sqlite3.c)
target_compile_options(sqlite PUBLIC
        -fno-strict-aliasing -Os)
set_target_properties(sqlite PROPERTIES
        ANDROID_ARM_MODE arm)
target_compile_definitions(sqlite PUBLIC
        NULL=0 SOCKLEN_T=socklen_t LOCALE_NOT_USED ANDROID_NDK DISABLE_IMPORTGL AVOID_TABLES ANDROID_TILE_BASED_DECODE HAVE_STRCHRNUL=0 ANDROID_ARMV6_IDCT)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        ${NATIVE_LIB}
        sqlite
        tgnet

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})
