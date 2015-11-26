cmake_minimum_required(VERSION 3.0)
project(grp_test)

include_directories(..)

add_executable(bitarray bitarray.c)
add_executable(graph graph.c)

target_link_libraries(bitarray grp)
target_link_libraries(graph grp)

add_test(bitarray bitarray)
add_test(graph graph)
