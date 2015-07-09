
CMAKE_MINIMUM_REQUIRED(VERSION 3.0)
PROJECT(grapher)

SUBDIRS(bitc)
SUBDIRS(bitc_test)

ENABLE_TESTING()

ADD_TEST(bitarray bitc_test/bitarray_test)
ADD_TEST(bitarray_big bitc_test/bitarray_test_big)
ADD_TEST(bitmatrix bitc_test/bitmatrix_test)
ADD_TEST(bitmatrix_big bitc_test/bitmatrix_test_big)
