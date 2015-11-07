CMAKE_MINIMUM_REQUIRED(VERSION 3.0)
PROJECT(grpstd)

SET(SOURCES
  bitarray.c
  graph.c)

SET(HEADERS
  bitarray.h
  graph.h)

ADD_LIBRARY(grpstd STATIC ${SOURCES})
SET_PROPERTY(TARGET grpstd PROPERTY ARCHIVE_OUTPUT_DIRECTORY ..)
SET_PROPERTY(TARGET grpstd PROPERTY C_STANDARD 11)
TARGET_LINK_LIBRARIES(grpstd m)

IF (test)
    ENABLE_TESTING()
    INCLUDE_DIRECTORIES(.)

    ADD_EXECUTABLE(test_bitarray tests/bitarray.c)
    TARGET_LINK_LIBRARIES(test_bitarray grpstd)
    ADD_TEST(bitarray test_bitarray)

    ADD_EXECUTABLE(test_graph tests/graph.c)
    TARGET_LINK_LIBRARIES(test_graph grpstd)
    ADD_TEST(graph test_graph)
ENDIF()
