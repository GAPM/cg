#ifndef GRPC_HELPER_H
#define GRPC_HELPER_H

struct arg_list {
    char *id;
    struct arg_list *next;
};

struct arg_list *new_arg_list(char *, struct arg_list *);
void free_arg_list(struct arg_list *);

#endif // GRPC_HELPER_H
