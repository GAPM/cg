#include <cassert>
#include <set>

#include "digraph.h"

using namespace std;

int main() {
    edge_set e = {{1, 2}, {2, 3}, {3, 1}, {1, 2}};
    assert(e.size() == 3);
}
