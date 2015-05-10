package edu.ucsb.cs290cloud.strategies;

import edu.ucsb.cs290cloud.commons.*;
import edu.ucsb.cs290cloud.ramseychecker.CliqueCounter;
import org.parse4j.ParseObject;

/**
 * Created by ethan_000 on 5/10/2015.
 */
public class Strategy1Random extends Strategy {
    public static final int TABOO_SIZE = 500;
    public static final int MAX_GRAPH_SIZE = 150;

    @Override
    public void runStrategy() {
        int cliquesCount, bestCliquesCount;
        FIFOEdge fifoEdge;

        GraphWithInfos graph = this.getInitialGraph();

        fifoEdge = new FIFOEdge(TABOO_SIZE);

        cliquesCount = new CliqueCounter(graph.getRawGraph())
                .getMonochromaticSubcliquesCount();
        bestCliquesCount = cliquesCount;
        this.setStrategyStatus(Strategy.Status.BEING_COMPUTED, graph);

        int i,j;
        while (graph.size() < MAX_GRAPH_SIZE) {
            // 0 subcliques so we found a counter example
            if (cliquesCount == 0) {
                System.out.println("Found a counter-example");
                System.out.println(graph.printGraph());
                System.out.println("--------------");

                this.setStrategyStatus(Strategy.Status.COUNTER_EXAMPLE, graph);
                return;
            }
            else {
                Pair<Integer, Integer> coord = graph.getRandomCoordInUpperTriangle();
                i = coord.getElement0();
                j = coord.getElement1();

                if (!fifoEdge.findEdge(i, j)) {
                    graph.flipValue(i, j);
                    // We check if number of cliques decreased: it's a good thing
                    cliquesCount = new CliqueCounter(graph.getRawGraph())
                            .getMonochromaticSubcliquesCountWithTerminate(bestCliquesCount);
                    if (cliquesCount < bestCliquesCount) {
                        fifoEdge.resetFIFO();
                        bestCliquesCount = cliquesCount;

                        System.out.print("Graph size: " + graph.size() + ", ");
                        System.out.print("Best count: " + bestCliquesCount + ", ");
                        System.out.print("Random edge: (" + i + "," + j + "), ");
                        System.out.print("New color: " + graph.getValue(i, j) + "\n");
                    } else {
                        graph.flipValue(i, j);//flip back
                    }
                    fifoEdge.insertEdge(i, j);
                    if(fifoEdge.getSize() >= graph.getSizeUpperTriangle())
                    {
                        System.out.print("Need to backtrack a little");
                        for(int k=0; k<3;k++) {
                            Pair<Integer, Integer> p = graph.getRandomCoordInUpperTriangle();
                            graph.flipValue(p.getElement0(), p.getElement1());
                        }
                        cliquesCount = new CliqueCounter(graph.getRawGraph())
                                .getMonochromaticSubcliquesCount();
                    }
                }
            }

        }
    }
}
