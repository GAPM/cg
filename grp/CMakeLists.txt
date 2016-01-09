cmake_minimum_required(VERSION 3.0)

project(grp)

add_library(grp STATIC
    base.c
    bitarray.c
    bitmatrix.c
    file.c
    graph.c
    str.c)
set_property(TARGET grp PROPERTY C_STANDARD 11)
target_link_libraries(grp m)

install(TARGETS grp DESTINATION lib)
install(FILES
    base.h
    bitarray.h
    bitmatrix.h
    file.h
    graph.h
    str.h
    DESTINATION include)

subdirs(tests)
