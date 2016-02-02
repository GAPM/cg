cmake_minimum_required(VERSION 3.0)
project(grp_test)

include_directories(..)

add_executable(base base.c)
add_executable(bitarray bitarray.c)
add_executable(bitmatrix bitmatrix.c)

set_property(TARGET base PROPERTY C_STANDARD 11)
set_property(TARGET bitarray PROPERTY C_STANDARD 11)
set_property(TARGET bitmatrix PROPERTY C_STANDARD 11)

target_link_libraries(base grp)
target_link_libraries(bitarray grp)
target_link_libraries(bitmatrix grp)

add_test(base base)
add_test(bitarray bitarray)
add_test(bitmatrix bitmatrix)
