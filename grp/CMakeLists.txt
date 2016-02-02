cmake_minimum_required(VERSION 3.0)

project(grp)

set(CMAKE_INSTALL_PREFIX dist)

add_library(grp STATIC
    base.c
    bitarray.c
    bitmatrix.c)
set_property(TARGET grp PROPERTY C_STANDARD 11)
target_link_libraries(grp m)

install(TARGETS grp DESTINATION lib)
install(FILES
    base.h
    bitarray.h
    bitmatrix.h
    DESTINATION include)

subdirs(tests)
enable_testing()
