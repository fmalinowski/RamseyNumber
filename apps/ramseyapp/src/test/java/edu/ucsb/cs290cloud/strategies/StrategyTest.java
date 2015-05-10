package edu.ucsb.cs290cloud.strategies;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.ucsb.cs290cloud.commons.GraphWithInfos;
import edu.ucsb.cs290cloud.commons.GraphFactory;

public class StrategyTest {
	
	//@Test
	public void testSetAndGetInitialGraph() {
		class StrategyChild extends Strategy {
			@Override
			public void runStrategy() {
			}
		};
		
		StrategyChild newStrategy;
		GraphWithInfos initialGraph, initialGraphInStrategy;
		
		initialGraph = GraphFactory.generateRandomGraph(2);
		initialGraph.setBestCount(2);
		newStrategy = new StrategyChild();
		
		assertEquals(Strategy.Status.NOT_YET_STARTED, newStrategy.getStrategyStatus());
		newStrategy.setInitialGraph(initialGraph);
		assertEquals(Strategy.Status.BEING_COMPUTED, newStrategy.getStrategyStatus());
		
		initialGraphInStrategy = newStrategy.getInitialGraph();
		assertNotSame(initialGraph, initialGraphInStrategy);
		assertEquals(initialGraph.getBestCount(), initialGraphInStrategy.getBestCount());
		assertTrue(initialGraph.equals(initialGraphInStrategy));
	}

	//@Test
	public void testInitialGraph_canRetrieveInitialGraphInTheOtherThread() {		
		class GraphContainer {
			public GraphWithInfos graphUsedInThread;
		};
		
		class StrategyChild extends Strategy {
			
			public GraphContainer gc;

			@Override
			public void runStrategy() {
				gc.graphUsedInThread = this.getInitialGraph();	
			}	
		};

		StrategyChild newStrategy;
		GraphContainer graphUsedInThread;
		GraphWithInfos initialGraph;
		
		graphUsedInThread = new GraphContainer();
		newStrategy = new StrategyChild();
		newStrategy.gc = graphUsedInThread;
		
		initialGraph = new GraphWithInfos(2);
		initialGraph.setValue(0, 0, 1);
		initialGraph.setValue(0, 1, 2);
		initialGraph.setValue(1, 0, 3);
		initialGraph.setValue(1, 1, 4);
		initialGraph.setBestCount(2);

		newStrategy.setInitialGraph(initialGraph);
		
		assertNull(graphUsedInThread.graphUsedInThread);
		newStrategy.run();
		try {
			newStrategy.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Make sure getInitialGraph was called and that it cloned the graph 
		// but it's not the same instance as the one provided by setInitialGraph
		assertNotNull(graphUsedInThread.graphUsedInThread);
		assertNotSame(initialGraph, graphUsedInThread.graphUsedInThread);

		// We check that the graph itself is the same
		assertEquals(1, graphUsedInThread.graphUsedInThread.getValue(0, 0));
		assertEquals(2, graphUsedInThread.graphUsedInThread.getValue(0, 1));
		assertEquals(3, graphUsedInThread.graphUsedInThread.getValue(1, 0));
		assertEquals(4, graphUsedInThread.graphUsedInThread.getValue(1, 1));
		
		// We check also that the other properties are the same
		assertEquals(initialGraph.getBestCount(), 
				graphUsedInThread.graphUsedInThread.getBestCount());
		
		// We make sure that the setInitialGraph modified the status of the strategy
		assertEquals(Strategy.Status.BEING_COMPUTED, newStrategy.getStrategyStatus());
	}
	
	//@Test
	public void testStrategyStatusAndGraph_canSetValuesInOtherThreadAndReadThemFromOtherThread() {
		class StrategyChild extends Strategy {

			public volatile Boolean continueInfiniteLoop = true;
			
			@Override
			public void runStrategy() {	
				synchronized(this) {
					GraphWithInfos graph = this.getInitialGraph();
					graph.flipValue(0, 0);
				
					this.setStrategyStatus(Strategy.Status.COUNTER_EXAMPLE, graph);
					
					notify();
				}
				// Simulate processing in thread
				while(continueInfiniteLoop);
			}
		};

		StrategyChild newStrategy;
		GraphWithInfos initialGraph, graphFromStrategy;
		
		initialGraph = new GraphWithInfos(2);
		initialGraph.setValue(0, 0, 0);
		initialGraph.setValue(0, 1, 1);
		initialGraph.setValue(1, 0, 1);
		initialGraph.setValue(1, 1, 0);
		initialGraph.setBestCount(2);
		
		newStrategy = new StrategyChild();
		newStrategy.setInitialGraph(initialGraph);
		assertEquals(Strategy.Status.BEING_COMPUTED, newStrategy.getStrategyStatus());
		
		newStrategy.start();
		synchronized(newStrategy) {
			try {
				newStrategy.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			assertEquals(Strategy.Status.COUNTER_EXAMPLE, newStrategy.getStrategyStatus());
			graphFromStrategy = newStrategy.getGraph();
			assertEquals(initialGraph.getValue(0, 1), graphFromStrategy.getValue(0, 1));
			assertEquals(initialGraph.getValue(1, 0), graphFromStrategy.getValue(1, 0));
			assertEquals(initialGraph.getValue(1, 1), graphFromStrategy.getValue(1, 1));
			assertNotEquals(initialGraph.getValue(0, 0), graphFromStrategy.getValue(0, 0));
			assertEquals(1, graphFromStrategy.getValue(0, 0));
		}
		newStrategy.continueInfiniteLoop = false;
	}
	
	//@Test
	public void testResetStrategy() {
		class StrategyChild extends Strategy {
			@Override
			public void runStrategy() {	
			}
		};
		
		StrategyChild strategy;
		GraphWithInfos graph;
		
		graph = GraphFactory.generateRandomGraph(2);
		
		strategy = new StrategyChild();
		strategy.setInitialGraph(graph);
		
		assertEquals(Strategy.Status.BEING_COMPUTED, strategy.getStrategyStatus());
		assertNotNull(strategy.getGraph());
		
		strategy.resetStrategy();
		assertEquals(Strategy.Status.NOT_YET_STARTED, strategy.getStrategyStatus());
		assertNull(strategy.getGraph());
	}

}
