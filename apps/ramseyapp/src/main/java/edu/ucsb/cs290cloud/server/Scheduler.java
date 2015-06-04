package edu.ucsb.cs290cloud.server;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ucsb.cs290cloud.commons.GraphWithInfos;
import edu.ucsb.cs290cloud.ramseychecker.CliqueCounter;
import edu.ucsb.cs290cloud.strategies.Strategy1Distributed;
import edu.ucsb.cs290cloud.commons.GraphFactory;

public class Scheduler {
	static Logger LOGGER = LoggerFactory.getLogger(Scheduler.class);

	public final static int INITIAL_GENERATED_GRAPH_SIZE = 20;
	private GraphsExplorer graphsExplorer;
	
	private int currentGraphID = 0;
	private GraphWithInfos lowestCountGraph;

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
			graphForClient = this.graphsExplorer
					.getGraphBeingComputedWithLowestBestCount(maxGraphBeingComputedSize);
			
			if (graphForClient.getSubmittedAt() < (System.currentTimeMillis()-120000)) {
				LOGGER.info("Stuck at best count:" + graphForClient.getBestCount());
				// Clear the graphBeingComputed list and set best count to be a very high value
				
				if (this.lowestCountGraph == null || this.lowestCountGraph.size() < graphForClient.size() || 
						(this.lowestCountGraph.size() == graphForClient.size() && 
						this.lowestCountGraph.getBestCount() > graphForClient.getBestCount())) {
					this.lowestCountGraph = graphForClient;
				}
				
				this.graphsExplorer.clearGraphsBeingComputedAtSize(graphForClient.size());
				graphForClient = this.lowestCountGraph.clone();
				graphForClient.flipRandomEdges();
				graphForClient.setBestCount(new CliqueCounter(graphForClient.getRawGraph())
				.getMonochromaticSubcliquesCount());
				
				this.currentGraphID++;
				graphForClient.setGraphID(this.currentGraphID);
				this.graphsExplorer.addGraphBeingComputed(graphForClient);
				LOGGER.info("Flipping some random edges. Best Count is now:" + graphForClient.getBestCount());
			}
			
			// Return the graph being computed that has its size just above
			// maxCounterExampleSize and with the lowest best count
			
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
		if (this.currentGraphID <= graphFromClient.getGraphID()) {
			this.graphsExplorer.addGraphBeingComputed(graphFromClient);
		}
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
