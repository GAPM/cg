/*
 * Copyright 2016 Simón Oroño & La Universidad del Zulia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sron.cg.lang.rt;

import sron.cg.lang.DiGraph;
import sron.cg.lang.Graph;

public class GOps {
    public static Graph gIntersection(Graph g1, Graph g2) {
        Error.setErr(ErrorType.NO_ERROR);

        if (g1.getSize() == g2.getSize()) {
            return g1.edgeIntersection(g2);
        }

        Error.setErr(ErrorType.GRAPH_SIZE_MISMATCH);
        return new Graph(0);
    }

    public static DiGraph dgIntersection(DiGraph g1, DiGraph g2) {
        Error.setErr(ErrorType.NO_ERROR);

        if (g1.getSize() == g2.getSize()) {
            return g1.edgeIntersection(g2);
        }

        Error.setErr(ErrorType.GRAPH_SIZE_MISMATCH);
        return new DiGraph(0);
    }

    public static Graph gUnion(Graph g1, Graph g2) {
        Error.setErr(ErrorType.NO_ERROR);

        if (g1.getSize() == g2.getSize()) {
            return g1.edgeUnion(g2);
        }

        Error.setErr(ErrorType.GRAPH_SIZE_MISMATCH);
        return new Graph(0);
    }

    public static DiGraph dgUnion(DiGraph g1, DiGraph g2) {
        Error.setErr(ErrorType.NO_ERROR);

        if (g1.getSize() == g2.getSize()) {
            return g1.edgeUnion(g2);
        }

        Error.setErr(ErrorType.GRAPH_SIZE_MISMATCH);
        return new DiGraph(0);
    }

    public static Graph gDifference(Graph g1, Graph g2) {
        Error.setErr(ErrorType.NO_ERROR);

        if (g1.getSize() == g2.getSize()) {
            return g1.edgeDifference(g2);
        }

        Error.setErr(ErrorType.GRAPH_SIZE_MISMATCH);
        return new Graph(0);
    }

    public static DiGraph dgDifference(DiGraph g1, DiGraph g2) {
        Error.setErr(ErrorType.NO_ERROR);

        if (g1.getSize() == g2.getSize()) {
            return g1.edgeDifference(g2);
        }

        Error.setErr(ErrorType.GRAPH_SIZE_MISMATCH);
        return new DiGraph(0);
    }
}
