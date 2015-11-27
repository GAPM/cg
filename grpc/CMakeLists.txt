cmake_minimum_required(VERSION 3.0)

find_package(FLEX REQUIRED)
find_package(BISON REQUIRED)

include_directories(${CMAKE_CURRENT_SOURCE_DIR})

if(NOT FLEX_FOUND)
    message(FATAL_ERROR "Flex not found")
endif()

if(NOT BISON_FOUND)
    message(FATAL_ERROR "Bison not found")
endif()

bison_target(parser parser.y ${CMAKE_CURRENT_BINARY_DIR}/parser.c)
flex_target(lexer lexer.l ${CMAKE_CURRENT_BINARY_DIR}/lexer.c)

add_executable(grpc
               ${FLEX_lexer_OUTPUTS}
               ${BISON_parser_OUTPUTS}
               ast.c cmd.c grpc.c)
set_property(TARGET grpc PROPERTY C_STANDARD 11)

install(TARGETS grpc DESTINATION bin)
