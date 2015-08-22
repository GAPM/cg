
CMAKE_MINIMUM_REQUIRED(VERSION 3.0)
PROJECT(bitcpp_test)

INCLUDE_DIRECTORIES(../bitcpp)

ADD_EXECUTABLE(bitcpp_test bitcpp_test.cpp)

TARGET_LINK_LIBRARIES(bitcpp_test bitcpp)
