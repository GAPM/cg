#ifndef GRPC_CMD_H
#define GRPC_CMD_H

#include <stdbool.h>

bool is_positional(char*);
int count_pos_args(int, char**);
char* get_pos_arg(int, char**, int);

#endif // GRPC_CMD_H
