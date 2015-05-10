package edu.ucsb.cs290cloud.strategies;

import edu.ucsb.cs290cloud.commons.Graph;
import edu.ucsb.cs290cloud.commons.GraphFactory;
import edu.ucsb.cs290cloud.commons.GraphWithInfos;
import edu.ucsb.cs290cloud.ramseychecker.CliqueCounter;
import edu.ucsb.cs290cloud.commons.FIFOEdge;

/**
 * Created by ethan_000 on 5/10/2015.
 */
public class Strategy1Random extends Strategy {
    public static final int TABOO_SIZE = 500;
    public static final int MAX_GRAPH_SIZE = 150;
    public static final int HIGH_LIMIT_CLIQUE_COUNT = 9999999;

    @Override
    public void runStrategy() {
        int graphSize, cliquesCount, bestCliquesCount, newValue;
        int best_i, best_j;
        FIFOEdge fifoEdge;

        fifoEdge = new FIFOEdge(TABOO_SIZE);
        best_i = -1;
        best_j = -1;

        GraphWithInfos graph = this.getInitialGraph();

        while (graph.size() < MAX_GRAPH_SIZE) {
            cliquesCount = new CliqueCounter(graph.getRawGraph())
                    .getMonochromaticSubcliquesCount();

            // 0 subcliques so we found a counter example
            if (cliquesCount == 0) {
                System.out.println("Found a counter-example");
                System.out.println(graph.printGraph());
                System.out.println("--------------");

                // Increase the size of the graph by one.
                graph = GraphFactory.generateRandomGraph(graph.size() + 1);

                // Reset the taboo list for the enw graph
                fifoEdge.resetFIFO();
            }
            else {
                // We flip an edge, record the new count and unflip the edge.
                // We'll remember the best flip and keep it next time
                // We work only with the upper oart of the matrix
                bestCliquesCount = HIGH_LIMIT_CLIQUE_COUNT;

                for (int i = 0; i < graph.size(); i++) {
                    for (int j = i + 1; j < graph.size(); j++) {

                        if(!fifoEdge.findEdge(i,j))
                        {
                            // We flip the value of the cell in the graph
                            graph.flipValue(i, j);

                            // We check if number of cliques decreased: it's a good thing
                            cliquesCount = new CliqueCounter(graph.getRawGraph())
                                    .getMonochromaticSubcliquesCountWithTerminate(bestCliquesCount);

                            if ((cliquesCount < bestCliquesCount)) {
                                bestCliquesCount = cliquesCount;
                                best_i = i;
                                best_j = j;
                            }
                            else if(cliquesCount == bestCliquesCount)
                            {
                                //add to list that will be randomized
                            }

                            // Set back the original value
                            graph.flipValue(i, j);
                        }

                    }
                }

                if (bestCliquesCount == HIGH_LIMIT_CLIQUE_COUNT) {
                    System.out.println("No best edge found, terminating");
                    return;
                }

                // We keep the best flip we saw
                graph.flipValue(best_i, best_j);

                // We taboo this configuration fo the graph so that we don't visit it again
                fifoEdge.insertEdge(best_i, best_j);


                System.out.print("Graph size: " + graph.size() + ", ");
                System.out.print("Best count: " + bestCliquesCount + ", ");
                System.out.print("Best edge: (" + best_i + "," + best_j + "), ");
                System.out.print("New color: " + graph.getValue(best_i, best_j) + "\n");
            }
        }

        fifoEdge.resetFIFO();

    }


}
