package edu.ucsb.cs290cloud.standalone;

import edu.ucsb.cs290cloud.ramseychecker.CliqueCounter;

public class CounterExamplesFinder {
	
	public static final int TABOO_SIZE = 500;
	public static final int MAX_GRAPH_SIZE = 102;
	public static final int HIGH_LIMIT_CLIQUE_COUNT = 9999999;
	
	public void startStrategy1(Graph graph) {
		int graphSize, cliquesCount, bestCliquesCount, newValue;
		int best_i, best_j;
		FIFOEdge fifoEdge;

		if(null==graph)
		{
			graphSize = 8;
			graph = new Graph(graphSize);
		}

		fifoEdge = new FIFOEdge(TABOO_SIZE);
		best_i = -1;
		best_j = -1;
		
		while (graph.size() < MAX_GRAPH_SIZE) {
			cliquesCount = new CliqueCounter(graph.getRawGraph())
				.getMonochromaticSubcliquesCount();
			
			// 0 subcliques so we found a counter example
			if (cliquesCount == 0) {
				System.out.println("Found a counter-example");
				System.out.println(graph.printGraph());
				System.out.println("--------------");
				
				// Increase the size of the graph by one.
				// The graph found becomes now a subgraph of the new one.
				// and the last row and last column contrain only 0s.
				graph = graph.copyGraph(graph.size() + 1);
				
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
						
						// We flip the value of the cell in the graph
						graph.flipValue(i, j);
						
						// We check if number of cliques decreased: it's a good thing
						cliquesCount = new CliqueCounter(graph.getRawGraph())
							.getMonochromaticSubcliquesCount();
						
						if ((cliquesCount < bestCliquesCount) && !fifoEdge.findEdge(i, j)) {
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

	public void startStrategy1_random(Graph graph) {
		int graphSize, cliquesCount, bestCliquesCount, newValue;
		int best_i, best_j;
		FIFOEdge fifoEdge;

		if(null==graph)
		{
			graphSize = 72;
			graph = GraphFactory.generateRandomGraph(graphSize);
		}

		fifoEdge = new FIFOEdge(TABOO_SIZE);
		best_i = -1;
		best_j = -1;

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
									.getMonochromaticSubcliquesCount();

							if ((cliquesCount < bestCliquesCount)) {
								bestCliquesCount = cliquesCount;
								best_i = i;
								best_j = j;
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
	
	public void startStrategy2(Graph graph) {
		int graphSize, cliquesCount, bestCliquesCount;
		int bestDistance;

		if(null == graph)
		{
			graphSize = 8;
			graph = new Graph(graphSize);
		}

		bestDistance = -1;
		
		while (graph.size() < MAX_GRAPH_SIZE) {
			cliquesCount = new CliqueCounter(graph.getRawGraph())
				.getMonochromaticSubcliquesCount();
			
			// 0 subcliques so we found a counter example
			if (cliquesCount == 0) {
				System.out.println("Found a counter-example");
				System.out.println(graph.printGraph());
				System.out.println("--------------");
				
				// Increase the size of the graph by one.
				// The graph found becomes now a subgraph of the new one.
				// and the last row and last column contrain only 0s.
				graph = graph.copyGraph(graph.size() + 1);
			}
			else {
				// We flip an edge, record the new count and unflip the edge.
				// We'll remember the best flip and keep it next time
				// We work only with the upper oart of the matrix
				bestCliquesCount = HIGH_LIMIT_CLIQUE_COUNT;
				
				for (int distance = 1; distance <= graph.size()/2; distance++) {
						
					// Flip the edges corresponding to this distance
					this.recolorEdgesSeparatedByDistance(graph, distance);
					
					// We check if number of cliques decreased: it's a good thing
					cliquesCount = new CliqueCounter(graph.getRawGraph())
						.getMonochromaticSubcliquesCount();
						
					if (cliquesCount < bestCliquesCount) {
						bestCliquesCount = cliquesCount;
						bestDistance = distance;
					}
					
					// Unflip the edges
					this.recolorEdgesSeparatedByDistance(graph, distance);
				}
				
				// Keep the best flip we saw
				this.recolorEdgesSeparatedByDistance(graph, bestDistance);
				
				System.out.print("Graph size: " + graph.size() + ", ");
				System.out.print("Best count: " + bestCliquesCount + ", ");
				System.out.print("Best distance: " + bestDistance + ", ");
				System.out.print("New color: " + graph.getValue(0, bestDistance) + "\n");
			}
		}
	}

	public void recolorEdgesSeparatedByDistance(Graph graph, int distance) {
		int i, x, y;
		
		for (i = 0; i < graph.size(); i++) {
			x = i;
			y = (i + distance) % graph.size();
			
			if (x < y) {
				graph.flipValue(x, y);
			}
			else {
				graph.flipValue(y, x);
			}
		}
	}
}
