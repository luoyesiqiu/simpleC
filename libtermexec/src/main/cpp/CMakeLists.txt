# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.10.2)

# Declares and names the project.

project("exec")

#添加库
add_library(exec
        SHARED
        process.cpp)

# 寻找已存在的库
find_library(log-lib log)
# 相当于链接操作
target_link_libraries( exec
        ${log-lib}
        )