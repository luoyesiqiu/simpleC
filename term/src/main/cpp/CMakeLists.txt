# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10.2)

# Declares and names the project.

project("term")

#添加库
add_library(term
        SHARED
        common.cpp fileCompat.cpp termExec.cpp)

# 寻找已存在的库
find_library(log-lib log)
# 相当于链接操作
target_link_libraries( term
        ${log-lib}
        )