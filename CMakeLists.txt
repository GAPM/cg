
CMAKE_MINIMUM_REQUIRED(VERSION 3.0)
PROJECT(grapher)

FIND_PACKAGE(BISON REQUIRED)
FIND_PACKAGE(FLEX REQUIRED)

SUBDIRS(bitc)
SUBDIRS(bitcpp)
SUBDIRS(bitc_test)
SUBDIRS(bitcpp_test)
SUBDIRS(std)
SUBDIRS(std_test)

ENABLE_TESTING()

ADD_TEST(bitc bitc_test/bitc_test)
ADD_TEST(bitcpp bitcpp_test/bitcpp_test)
ADD_TEST(std_test std_test/std_test)
