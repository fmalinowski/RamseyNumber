package edu.ucsb.cs290cloud.server;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.LinkedList;

import edu.ucsb.cs290cloud.commons.GraphWithInfos;
import edu.ucsb.cs290cloud.commons.Message;
import edu.ucsb.cs290cloud.ramseychecker.CliqueCounter;
import edu.ucsb.cs290cloud.strategies.Strategy1Distributed;
import edu.ucsb.cs290cloud.commons.GraphFactory;

public class Scheduler {
	
	class ClientWrapper {
		SocketAddress clientSocketAddress;
		long lastUpdate;
	}

	public final static int INITIAL_GENERATED_GRAPH_SIZE = 20;
	private GraphsExplorer graphsExplorer;
	
	private LinkedList<ClientWrapper> strategy1DistClients;

	public Scheduler(GraphsExplorer graphsExplorer) {
		this.graphsExplorer = graphsExplorer;
		this.strategy1DistClients = new LinkedList<Scheduler.ClientWrapper>();
	}

	/**
	 * It is called the first time the client connects to master.
	 * 
	 * @return the first graph that should be computed by the client.
	 */
	protected synchronized GraphWithInfos getNewTask() {
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
	
	public synchronized Message processReadyMessage(Message messageFromClient, SocketAddress clientSocketAddress) {
		Message message;
		
		message = new Message();
		message.setGraph(this.getNewTask());
		message.setMessage("NEWGRAPH");
		
		if (messageFromClient.getStrategyClass().equals(Strategy1Distributed.class)) {
			HashMap<String, String> strategyParametersForClient;
			
			strategyParametersForClient = this.getInstructionsForStrategy1Distributed(clientSocketAddress);
			message.setStrategyParameters(strategyParametersForClient);
			message.setStrategyClass(Strategy1Distributed.class);
		}
		
		return message;
	}

	/**
	 * Called when a client has found a counter example
	 * 
	 * @return the new graph that should be computed by the client wrapped in a message
	 */
	public synchronized Message processFoundCounterExample(Message messageFromClient, SocketAddress clientSocketAddress) {
		// Store Counter Example
		// Get graph Max Size of Counter Examples and get graph being computed
		// with lowest
		// best count that is one size bigger than the graph max size of counter
		// example
		// Otherwise if no graph being computed, we generate new graph that is
		// one size bigger
		// than the biggest counter example size
		Message message;
		GraphWithInfos graphFromClient;
		CliqueCounter cliqueCounter;
		
		message = new Message();
		
		graphFromClient = messageFromClient.getGraph();
		
		// Check if it's really a counter example before saving it as a counter example
		cliqueCounter = new CliqueCounter(graphFromClient.getRawGraph());
		if (cliqueCounter.getMonochromaticSubcliquesCount() == 0) {
			this.graphsExplorer.addNewCounterExample(graphFromClient);
		}
		
		message.setGraph(this.getNewTask());
		message.setMessage("NEWGRAPH");
		
		if (messageFromClient.getStrategyClass().equals(Strategy1Distributed.class)) {
			HashMap<String, String> strategyParametersForClient;
			
			strategyParametersForClient = this.getInstructionsForStrategy1Distributed(clientSocketAddress);
			message.setStrategyParameters(strategyParametersForClient);
			message.setStrategyClass(Strategy1Distributed.class);
		}
		
		return message;
	}

	/**
	 * Called when the client sends a periodical update about its computation
	 * 
	 * @return null if the client should continue its work on the current matrix
	 *         otherwise it returns the new graph that should be computed by the
	 *         client
	 */
	public synchronized Message processStatusUpdateFromClient(Message messageFromClient, SocketAddress clientSocketAddress) {
		// Store Graph being Computed
		// Get graph Max Size of Counter Examples and get graph being computed
		// with lowest
		// best count that is one size bigger than the graph max size of counter
		// example
		Message message;
		GraphWithInfos graphFromClient, newGraphForClient;
		
		message = new Message();
		
		graphFromClient = messageFromClient.getGraph();
		
		this.graphsExplorer.addGraphBeingComputed(graphFromClient);
		newGraphForClient = this.getNewTask();
		
		// If best graph being computed is the one just committed by the client,
		// the client will continue on his graph.
		if (newGraphForClient.equals(graphFromClient)) {
			message.setMessage("CONTINUE");
		}
		else {
			message.setMessage("NEWGRAPH");
			message.setGraph(newGraphForClient);
		}
		
		if (messageFromClient.getStrategyClass().equals(Strategy1Distributed.class)) {
			int clientPositionBeforeListUpdate, numberOfMatrixSplitsBeforeListUpdate;
			int numberOfMatrixSplits, clientPositionInMatrixSplit;
			HashMap<String, String> strategyParametersFromClient, strategyParametersForClient;
			
			strategyParametersFromClient = messageFromClient.getStrategyParameters();
			clientPositionBeforeListUpdate = Integer.parseInt(strategyParametersFromClient.get("clientPositionInMatrixSplit"));
			numberOfMatrixSplitsBeforeListUpdate = Integer.parseInt(strategyParametersFromClient.get("clientPositionInMatrixSplit"));
			
			strategyParametersForClient = this.getInstructionsForStrategy1Distributed(clientSocketAddress);
			numberOfMatrixSplits = Integer.parseInt(strategyParametersForClient.get("numberOfMatrixSplits"));
			clientPositionInMatrixSplit = Integer.parseInt(strategyParametersForClient.get("clientPositionInMatrixSplit")); 
			
			if ((numberOfMatrixSplits != numberOfMatrixSplitsBeforeListUpdate) 
					|| (clientPositionInMatrixSplit != clientPositionBeforeListUpdate)) {
				message.setMessage("NEWGRAPH");
				message.setGraph(graphFromClient);
			}
			
			message.setStrategyParameters(strategyParametersForClient);
			message.setStrategyClass(Strategy1Distributed.class);
		}
		
		return message;
	}
	
	protected synchronized HashMap<String, String> getInstructionsForStrategy1Distributed(SocketAddress clientSocketAddress) {
		HashMap<String, String> strategyParametersForClient;
		ClientWrapper clientWrapper;
		int positionOfClientWrapper;
		
		strategyParametersForClient = new HashMap<String, String>();
		positionOfClientWrapper = -1;
		
		// Check if one client has an outdated lastUpdate and delete him from list
		for (int i = 0; i < this.strategy1DistClients.size(); i++) {
			clientWrapper = this.strategy1DistClients.get(i);
			
			if (clientWrapper.lastUpdate < (System.currentTimeMillis() - 30000)) {
				this.strategy1DistClients.remove(i);
				i--;
			}
		}
		
		// Update clientWrapper lastUpdate or add it to the linkedlist
		for (int i = 0; i < this.strategy1DistClients.size(); i++) {
			clientWrapper = this.strategy1DistClients.get(i);
			
			if (clientSocketAddress.equals(clientWrapper.clientSocketAddress)) {
				clientWrapper.lastUpdate = System.currentTimeMillis();
				positionOfClientWrapper = i + 1;
				break;
			}
		}
		
		// If client is not in list, we add it at the end of the list
		if (positionOfClientWrapper < 0) {
			clientWrapper = new ClientWrapper();
			clientWrapper.clientSocketAddress = clientSocketAddress;
			clientWrapper.lastUpdate = System.currentTimeMillis();
			this.strategy1DistClients.add(clientWrapper);
			positionOfClientWrapper = this.strategy1DistClients.size();
		}
		
		strategyParametersForClient.put("numberOfMatrixSplits", String.valueOf(this.strategy1DistClients.size()));
		strategyParametersForClient.put("clientPositionInMatrixSplit", String.valueOf(positionOfClientWrapper));
		
		return strategyParametersForClient;
	}
	
	protected int getPositionOfClientInStrategy1DistributedList(SocketAddress clientSocketAddress) {
		ClientWrapper clientWrapper;
		int positionOfClientWrapper;
		
		positionOfClientWrapper = -1;
		
		// Update clientWrapper lastUpdate or add it to the linkedlist
		for (int i = 0; i < this.strategy1DistClients.size(); i++) {
			clientWrapper = this.strategy1DistClients.get(i);
					
			if (clientSocketAddress.equals(clientWrapper.clientSocketAddress)) {
				positionOfClientWrapper = i + 1;
				break;
			}
		}
		
		return positionOfClientWrapper;
	}
	
	protected int getNumberOfClientsInStrategy1DistributedList() {
		return this.strategy1DistClients.size();
	}

}
