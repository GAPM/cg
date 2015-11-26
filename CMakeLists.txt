cmake_minimum_required(VERSION 3.0)
project(grapher)

set(CMAKE_INSTALL_PREFIX dist)

subdirs(grp)
subdirs(grpc)

enable_testing()
