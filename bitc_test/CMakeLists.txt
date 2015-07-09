
CMAKE_MINIMUM_REQUIRED(VERSION 3.0)
PROJECT(bitc_test)

INCLUDE_DIRECTORIES(../bitc)

ADD_EXECUTABLE(bitarray_test bitarray_test.c)
ADD_EXECUTABLE(bitmatrix_test bitmatrix_test.c)
ADD_EXECUTABLE(bitarray_test_big bitarray_test_big.c)
ADD_EXECUTABLE(bitmatrix_test_big bitmatrix_test_big.c)
TARGET_LINK_LIBRARIES(bitarray_test bitc)
TARGET_LINK_LIBRARIES(bitmatrix_test bitc)
TARGET_LINK_LIBRARIES(bitarray_test_big bitc)
TARGET_LINK_LIBRARIES(bitmatrix_test_big bitc)
