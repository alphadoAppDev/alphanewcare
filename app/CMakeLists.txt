cmake_minimum_required(VERSION 3.4.1)

#set(projectPath C:/Projects/App/AlphaCare/AlphaCare)
#set(projectPath C:/Users/dwyi8/Documents/ursdetector_android)
#set(projectPath /Users/kim/1_AOS/AlphaCare)
#set(projectPath /Users/kim/1_AOS/alphanewcare)
set(projectPath /Users/alphado/AndroidStudioProjects/alphanewcar/alphanewcare)

set(pathLIBOPENCV ${projectPath}/app/src/main/jniLibs/${ANDROID_ABI}/libopencv_java4.so)

set(CMAKE_VERBOSE_MAKEFILE on)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

include_directories(${projectPath}/app/src/main/cpp/opencv/include)
include_directories(${projectPath}/app/src/main/cpp/)

AUX_SOURCE_DIRECTORY(src/main/cpp SOURCES)

add_library(native-lib SHARED ${SOURCES})

add_library(lib_opencv SHARED IMPORTED)

set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION ${pathLIBOPENCV})

find_library(log-lib log)

target_link_libraries(native-lib lib_opencv ${log-lib})