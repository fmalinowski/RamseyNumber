package edu.ucsb.cs290cloud.strategies;

import edu.ucsb.cs290cloud.commons.GraphWithInfos;
import edu.ucsb.cs290cloud.ramseychecker.CliqueCounter;
import edu.ucsb.cs290cloud.commons.FIFOEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Strategy1 extends Strategy {
	static Logger LOGGER = LoggerFactory.getLogger(Strategy1.class);

	public static final int TABOO_SIZE = 500;
	public static final int MAX_GRAPH_SIZE = 150;
	public static final int HIGH_LIMIT_CLIQUE_COUNT = 9999999;
	
	@Override
	public void runStrategy() {
		GraphWithInfos graph;
		FIFOEdge fifoEdge;
		int cliquesCount, bestCliquesCount;
		int best_i, best_j;
		
		graph = this.getInitialGraph();

		fifoEdge = new FIFOEdge(TABOO_SIZE);
		best_i = -1;
		best_j = -1;

		while (graph.size() < MAX_GRAPH_SIZE) {
			cliquesCount = new CliqueCounter(graph.getRawGraph())
					.getMonochromaticSubcliquesCount();

			// 0 subclique so we found a counter example
			if (cliquesCount == 0) {
				// BEGIN OF DEBUG PRINTS AREA
				LOGGER.info("Found a counter-example");
				LOGGER.info("size: " + graph.size());
				LOGGER.info(graph.printGraph());
				LOGGER.info("--------------");
				// END OF DEBUG PRINTS AREA
				
				// Save the counter example and end the computation because  
				// we have found a counter example
				graph.setBestCount(0);
				this.setStrategyStatus(Strategy.Status.COUNTER_EXAMPLE, graph);
				return;
				
			} else {
				// We flip an edge, record the new count and unflip the edge.
				// We'll remember the best flip and keep it next time
				// We work only with the upper part of the matrix
				bestCliquesCount = HIGH_LIMIT_CLIQUE_COUNT;

				for (int i = 0; i < graph.size(); i++) {
					for (int j = i + 1; j < graph.size(); j++) {

						// We flip the value of the cell in the graph
						graph.flipValue(i, j);

						// We check if number of cliques decreased: it's a good
						// thing
						cliquesCount = new CliqueCounter(graph.getRawGraph())
								.getMonochromaticSubcliquesCountWithTerminate(bestCliquesCount);

						if ((cliquesCount < bestCliquesCount)
								&& !fifoEdge.findEdge(i, j)) {
							bestCliquesCount = cliquesCount;
							best_i = i;
							best_j = j;
						}

						// Set back the original value
						graph.flipValue(i, j);
					}
				}

				if (bestCliquesCount == HIGH_LIMIT_CLIQUE_COUNT) {
					LOGGER.info("No best edge found, terminating");
					return;
				}

				// We keep the best flip we saw
				graph.flipValue(best_i, best_j);

				// We taboo this configuration for the graph so that we don't
				// visit it again
				fifoEdge.insertEdge(best_i, best_j);
				
				// Save the latest best graph being computed
				graph.setBestCount(bestCliquesCount);
				this.setStrategyStatus(Strategy.Status.BEING_COMPUTED, graph);

				// BEGIN OF DEBUG PRINTS AREA
				LOGGER.debug("Graph size: " + graph.size() + ", ");
				LOGGER.debug("Best count: " + bestCliquesCount + ", ");
				LOGGER.debug("Best edge: (" + best_i + "," + best_j + "), ");
				LOGGER.debug("New color: " + graph.getValue(best_i, best_j)
						+ "\n");
				// END OF DEBUG PRINTS AREA
			}
		}

		fifoEdge.resetFIFO();
	}

}
