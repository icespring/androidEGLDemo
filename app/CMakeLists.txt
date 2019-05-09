cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -Wall")

file(GLOB source "src/main/cpp/*.cpp")


add_library(gl2jni SHARED
        "src/main/cpp/native_render.cpp")

add_library(nativeegl SHARED
        "src/main/cpp/jniapi.cpp"
        "src/main/cpp/renderer.cpp"
        )

# add lib dependencies
target_link_libraries(gl2jni
                      android
                      log
                      EGL
                      GLESv2)

target_link_libraries(nativeegl
        android
        log
        EGL
        #        GLESv1_CM
        GLESv2
        )
