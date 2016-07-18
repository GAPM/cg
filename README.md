# cg

[![codebeat badge](https://codebeat.co/badges/6127d066-2bdf-437c-a3cc-503725af11a1)](https://codebeat.co/projects/github-com-simonorono-cg)

Compact Graph Programming Language

cg is a programming language for manipulation of directed and undirected graphs (only non-weighted).

Example:

```go
// Single line comment
/*
 * Multi line comment
 */

var i int = 7; // Global int variable

// Function definition
func sum(a int, b int) int {
  return a + b + i;
}

func main() {
  var g graph;
  var dg digraph;

  g = graph [4] {
    [0, 2],
    [1, 3]
  }; // Graph literals
  
  dg = digraph [3] {
    [0, 1],
    [1, 2],
    [2, 0]
  }; // Graphs nodes are accessed by index (starting from 0)
  
  print sum(3, 4); // Print statement (prints 14)
  
  g_remove_loops(g);
  dg_remove_loops(dg);
  
  // If statement
  if (sum(2, 2) == 12) {
    print "Yay! I guessed.";
  } else {
    print "Nope";
  } // Prints "Nope"
  
  var sum int; // Defaults to 0
  for (i = 0; i <= 100; i += 1) {
    // Uses the global variable i
    sum += i;
  }
  print sum; // Prints 5050
  
  // While statements
  var epsilon float = 1;
  while (epsilon + 1.0 > 1.0) {
    epsilon /= 2.0;
  }
  print epsilon; // Prints 5.9604645E-8
}
```
