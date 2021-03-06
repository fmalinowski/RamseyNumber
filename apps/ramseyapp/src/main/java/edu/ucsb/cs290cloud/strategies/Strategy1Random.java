package edu.ucsb.cs290cloud.strategies;

import edu.ucsb.cs290cloud.commons.*;
import edu.ucsb.cs290cloud.ramseychecker.CliqueCounter;
import org.parse4j.ParseObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ethan_000 on 5/10/2015.
 */
public class Strategy1Random extends Strategy {
    public static final int TABOO_SIZE = 500;
    public static final int MAX_GRAPH_SIZE = 150;
    static Logger LOGGER = LoggerFactory.getLogger(Strategy1Random.class);
    @Override
    public void runStrategy() {
        int cliquesCount, bestCliquesCount;
        FIFOEdge fifoEdge;
        int best_i,best_j;

        GraphWithInfos graph = this.getInitialGraph();

        fifoEdge = new FIFOEdge(graph.getSizeUpperTriangle());

        cliquesCount = new CliqueCounter(graph.getRawGraph())
                .getMonochromaticSubcliquesCount();
        bestCliquesCount = cliquesCount;
        graph.setBestCount(cliquesCount);
        this.setStrategyStatus(Strategy.Status.BEING_COMPUTED, graph);

        int i,j;
        while (this.isThreadAlive() && graph.size() < MAX_GRAPH_SIZE) {
            // 0 subcliques so we found a counter example
            if (cliquesCount == 0) {
                LOGGER.info("Found a counter-example\n" + graph.printGraph() + "\n----------");
                graph.setBestCount(0);
                this.setStrategyStatus(Strategy.Status.COUNTER_EXAMPLE, graph);
                return;
            }
            else if(fifoEdge.getSize() < .75*fifoEdge.getMaxFIFOSize()) {

                Pair<Integer, Integer> coord = graph.getRandomCoord();
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
                        graph.setBestCount(cliquesCount);

                        LOGGER.info("Graph size: " + graph.size() + ", ");
                        LOGGER.info("Best count: " + bestCliquesCount + ", ");
                        LOGGER.info("Random edge: (" + i + "," + j + "), ");
                        LOGGER.info("New color: " + graph.getValue(i, j) + "), ");
                        LOGGER.info("\n");

                    } else {
                        graph.flipValue(i, j);//flip back
                    }
                    fifoEdge.insertEdge(i, j);

                }
            }
            else //revert to strategy 1 so we dont waste too much time trying to randomly select
            {
                best_i = best_j = 0;
                for (i = 0; i < graph.size(); i++) {
                    for (j = i + 1; j < graph.size(); j++) {
                        if(!fifoEdge.findEdge(i, j))
                        // We flip the value of the cell in the graph
                        graph.flipValue(i, j);

                        // We check if number of cliques decreased: it's a good
                        // thing
                        cliquesCount = new CliqueCounter(graph.getRawGraph())
                                .getMonochromaticSubcliquesCountWithTerminate(bestCliquesCount);

                        if ((cliquesCount < bestCliquesCount)){
                            bestCliquesCount = cliquesCount;
                            best_i = i;
                            best_j = j;
                        }
                        //fifoEdge.insertEdge(i,j);

                        // Set back the original value
                        graph.flipValue(i, j);
                    }
                }

                fifoEdge.resetFIFO();

                if(best_i == 0 && best_j == 0)
                {
                    LOGGER.info("Need to backtrack a little");
                    for(int k=0; k<1;k++) {
                        Pair<Integer, Integer> p = graph.getRandomCoord();
                        graph.flipValue(p.getElement0(), p.getElement1());
                    }
                    cliquesCount = new CliqueCounter(graph.getRawGraph())
                            .getMonochromaticSubcliquesCount();
                    bestCliquesCount = cliquesCount;
                }else
                {
                    graph.flipValue(best_i, best_j);
                    fifoEdge.insertEdge(best_i,best_j);
                    cliquesCount = bestCliquesCount;
                    graph.setBestCount(cliquesCount);
                }
            }
        }
    }
}
