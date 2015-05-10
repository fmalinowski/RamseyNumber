package edu.ucsb.cs290cloud.client;

import edu.ucsb.cs290cloud.commons.GraphWithInfos;
import edu.ucsb.cs290cloud.ramseychecker.CliqueCounter;
import edu.ucsb.cs290cloud.commons.FIFOEdge;

public class ClientCounterEx implements Runnable {

	public static final int TABOO_SIZE = 500;
	public static final int MAX_GRAPH_SIZE = 102;
	public static final int HIGH_LIMIT_CLIQUE_COUNT = 9999999;
	public boolean foundCounterEx = false;
	public boolean firstCounterEx = true;
	private GraphWithInfos currentGraph = null;
	private GraphWithInfos bestGraph = null;

	private void clientCounterExFinder(GraphWithInfos graph) {
		int graphSize, cliquesCount, bestCliquesCount, newValue;
		int best_i, best_j;
		FIFOEdge fifoEdge;

//		if (null == graph) {
//			graphSize = 8;
//			graph = new GraphWithInfos(graphSize);
//		}

		System.out.println("entered graph size" + graph.size());
		
		fifoEdge = new FIFOEdge(TABOO_SIZE);
		best_i = -1;
		best_j = -1;

		while (graph.size() < MAX_GRAPH_SIZE) {
			cliquesCount = new CliqueCounter(graph.getRawGraph())
					.getMonochromaticSubcliquesCount();

			// 0 subcliques so we found a counter example
			if (cliquesCount == 0) {
				System.out.println("Found a counter-example");
				foundCounterEx = true;
				// System.out.println(graph.printGraph());
				// System.out.println("--------------");

				// Increase the size of the graph by one.
				// The graph found becomes now a subgraph of the new one.
				// and the last row and last column contrain only 0s.
				graph = graph.copyGraph(graph.size() + 1);
				bestGraph = graph;
				// Reset the taboo list for the enw graph
				fifoEdge.resetFIFO();
				return;
			} else {
				// We flip an edge, record the new count and unflip the edge.
				// We'll remember the best flip and keep it next time
				// We work only with the upper oart of the matrix
				bestCliquesCount = HIGH_LIMIT_CLIQUE_COUNT;

				for (int i = 0; i < graph.size(); i++) {
					for (int j = i + 1; j < graph.size(); j++) {

						// We flip the value of the cell in the graph
						graph.flipValue(i, j);

						// We check if number of cliques decreased: it's a good
						// thing
						cliquesCount = new CliqueCounter(graph.getRawGraph())
								.getMonochromaticSubcliquesCount();

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
					System.out.println("No best edge found, terminating");
					return;
				}

				// We keep the best flip we saw
				graph.flipValue(best_i, best_j);

				// We taboo this configuration fo the graph so that we don't
				// visit it again
				fifoEdge.insertEdge(best_i, best_j);

				System.out.print("Graph size: " + graph.size() + ", ");
				System.out.print("Best count: " + bestCliquesCount + ", ");
				System.out
						.print("Best edge: (" + best_i + "," + best_j + "), ");
				System.out.print("New color: " + graph.getValue(best_i, best_j)
						+ "\n");
			}
		}

		fifoEdge.resetFIFO();
	}

	public void run() {
		clientCounterExFinder(currentGraph);
	}

	public String getStatus() {
		if(foundCounterEx) {
//			firstCounterEx = false;
			return "COUNTEREXAMPLE";
		} else return "STATUS";
	}

	public GraphWithInfos getGraph() {
		return bestGraph;
	}

	public void setNewGraph(GraphWithInfos graph) {
		currentGraph = graph;
		System.out.println("Current graph\n"+currentGraph);
	}
}
