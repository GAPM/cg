#ifndef GRAPHER_STD_BASE
#define GRAPHER_STD_BASE

#include <set>
#include <string>

struct vertex {
    unsigned int id;
    std::string label;

    vertex(unsigned int id, std::string label) {
        this->id = id;
        this->label = label;
    }
};

struct edge {
    unsigned int src;
    unsigned int dst;

    edge(unsigned int s, unsigned int d) {
        this->src = s;
        this->dst = d;
    }
};

// Functor used to compare inequality of edges
struct edge_not_eq {
    bool operator() (const edge& fst, const edge& snd) {
        return (fst.src != snd.src)
                && (fst.dst != snd.dst);
    }
};

typedef std::set<vertex> vertex_set;
typedef std::set<edge, edge_not_eq> edge_set;

#endif //GRAPHER_STD_BASE
