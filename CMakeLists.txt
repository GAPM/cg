
CMAKE_MINIMUM_REQUIRED(VERSION 3.0)
PROJECT(grapher)

SUBDIRS(bitc)
SUBDIRS(bitc_test)

ENABLE_TESTING()

ADD_TEST(basic bitc_test/bitc_test)
