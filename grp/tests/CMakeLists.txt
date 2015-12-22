cmake_minimum_required(VERSION 3.0)
project(grp_test)

include_directories(..)

add_executable(bitarray bitarray.c)
add_executable(graph graph.c)
add_executable(str str.c)

target_link_libraries(bitarray grp)
target_link_libraries(graph grp)
target_link_libraries(str grp)

add_test(bitarray bitarray)
add_test(graph graph)
add_test(str str)
