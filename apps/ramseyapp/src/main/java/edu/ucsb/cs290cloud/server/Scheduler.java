package edu.ucsb.cs290cloud.server;

import java.util.LinkedList;

import edu.ucsb.cs290cloud.commons.GraphWithInfos;
import edu.ucsb.cs290cloud.ramseychecker.CliqueCounter;
import edu.ucsb.cs290cloud.commons.GraphFactory;

public class Scheduler {

	public final static int INITIAL_GENERATED_GRAPH_SIZE = 20;
	private GraphsExplorer graphsExplorer;

	public Scheduler(GraphsExplorer graphsExplorer) {
		this.graphsExplorer = graphsExplorer;
	}

	/**
	 * It is called the first time the client connects to master.
	 * 
	 * @return the first graph that should be computed by the client.
	 */
	public synchronized GraphWithInfos getNewTask() {
		GraphWithInfos graphForClient;
		LinkedList<GraphWithInfos> counterExamples;
		int maxCounterExampleSize, maxGraphBeingComputedSize;

		graphForClient = null;
		maxCounterExampleSize = this.graphsExplorer.getMaxCounterExamplesSize();
		maxGraphBeingComputedSize = this.graphsExplorer
				.getMaxGraphBeingComputedSize();

		if ((maxCounterExampleSize == 0) && (maxGraphBeingComputedSize == 0)) {
			// Generate an initial graph of size X (it happens when we just
			// launch master)
			graphForClient = GraphFactory
					.generateRandomGraph(INITIAL_GENERATED_GRAPH_SIZE);
		} else if (maxGraphBeingComputedSize <= maxCounterExampleSize) {
			// Generate new graph from counter Example
			counterExamples = this.graphsExplorer
					.getCounterExamples(maxCounterExampleSize);
			graphForClient = counterExamples.getFirst();
			graphForClient = graphForClient
					.copyGraph(maxCounterExampleSize + 1);
		} else {
			// Return the graph being computed that has its size just above
			// maxCounterExampleSize and with the lowest best count
			graphForClient = this.graphsExplorer
					.getGraphBeingComputedWithLowestBestCount(maxGraphBeingComputedSize);
		}

		return graphForClient;
	}

	/**
	 * Called when a client has found a counter example
	 * 
	 * @return the new graph that should be computed by the client
	 */
	public synchronized GraphWithInfos processFoundCounterExample(GraphWithInfos graphFromClient) {
		// Store Counter Example
		// Get graph Max Size of Counter Examples and get graph being computed
		// with lowest
		// best count that is one size bigger than the graph max size of counter
		// example
		// Otherwise if no graph being computed, we generate new graph that is
		// one size bigger
		// than the biggest counter example size
		CliqueCounter cliqueCounter;
		
		// Check if it's really a counter example before saving it as a counter example
		cliqueCounter = new CliqueCounter(graphFromClient.getRawGraph());
		if (cliqueCounter.getMonochromaticSubcliquesCount() == 0) {
			this.graphsExplorer.addNewCounterExample(graphFromClient);
		}
		return this.getNewTask();
	}

	/**
	 * Called when the client sends a periodical update about its computation
	 * 
	 * @return null if the client should continue its work on the current matrix
	 *         otherwise it returns the new graph that should be computed by the
	 *         client
	 */
	public synchronized GraphWithInfos processStatusUpdateFromClient(GraphWithInfos graphFromClient) {
		// Store Graph being Computed
		// Get graph Max Size of Counter Examples and get graph being computed
		// with lowest
		// best count that is one size bigger than the graph max size of counter
		// example
		GraphWithInfos newGraphForClient;
		this.graphsExplorer.addGraphBeingComputed(graphFromClient);
		newGraphForClient = this.getNewTask(); 
		
		// If best graph being computed is the one just committed by the client,
		// the client will continue on his graph.
		if (newGraphForClient.equals(graphFromClient)) {
			return null;
		}
		else {
			return newGraphForClient;
		}
	}

}
