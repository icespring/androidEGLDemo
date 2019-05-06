cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -Wall")

file(GLOB source "src/main/cpp/*.cpp")


add_library(gl2jni SHARED
            ${source})

# add lib dependencies
target_link_libraries(gl2jni
                      android
                      log
                      EGL
                      GLESv2)