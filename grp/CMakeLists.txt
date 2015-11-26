cmake_minimum_required(VERSION 3.0)

project(grp)

add_library(grp STATIC
    bitarray.c
    bitmatrix.c
    graph.c)
set_property(TARGET grp PROPERTY C_STANDARD 11)
target_link_libraries(grp m)

install(TARGETS grp DESTINATION lib)
install(FILES
    bitarray.h
    bitmatrix.h
    graph.h
    DESTINATION include)

subdirs(tests)
