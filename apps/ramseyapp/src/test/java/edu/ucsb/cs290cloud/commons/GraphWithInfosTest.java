package edu.ucsb.cs290cloud.commons;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.ucsb.cs290cloud.standalone.GraphFactory;

public class GraphWithInfosTest {

	@Test
	public void testClone() {
		GraphWithInfos graph, clonedGraph;
		
		graph = GraphFactory.generateRandomGraph(2);
		graph.setBestCount(2);
		graph.setGraphID(1234);
		graph.setParentGraphID(12345);
		graph.setStatus(GraphWithInfos.Status.COUNTER_EXAMPLE_FOUND_BUT_NOT_SAVED);
		graph.setStrategyUsed("lazy ass strategy");
		graph.setTimeSpentOnBestCount(12);
		
		clonedGraph = graph.clone();
		
		assertNotSame(graph, clonedGraph);
		assertEquals(graph.getBestCount(), clonedGraph.getBestCount());
		assertEquals(graph.getGraphID(), clonedGraph.getGraphID());
		assertEquals(graph.getParentGraphID(), clonedGraph.getParentGraphID());
		assertEquals(graph.getStatus(), clonedGraph.getStatus());
		assertEquals(graph.getStrategyUsed(), clonedGraph.getStrategyUsed());
		assertEquals(graph.getTimeSpentOnBestCount(), clonedGraph.getTimeSpentOnBestCount());
		
		clonedGraph.setValue(0, 0, 1);
		graph.setValue(0, 0, 2);
		assertNotEquals(graph.getValue(0, 0), clonedGraph.getValue(0, 0));
	}
	
	@Test
	public void testEquals() {
		GraphWithInfos graph, clonedGraph;
		
		graph = GraphFactory.generateRandomGraph(2);
		graph.setBestCount(2);
		graph.setGraphID(1234);
		graph.setParentGraphID(12345);
		graph.setStatus(GraphWithInfos.Status.COUNTER_EXAMPLE_FOUND_BUT_NOT_SAVED);
		graph.setStrategyUsed("lazy ass strategy");
		graph.setTimeSpentOnBestCount(12);
		
		clonedGraph = graph.clone();
		// We modify all the fields except the graph itself.
		graph.setGraphID(0);
		graph.setParentGraphID(0);
		graph.setStatus(GraphWithInfos.Status.COUNTER_EXAMPLE_SAVED);
		graph.setStrategyUsed("dumb ass");
		graph.setTimeSpentOnBestCount(0);
		
		assertTrue(graph.equals(clonedGraph));
		clonedGraph.flipValue(0, 0);
		assertNotEquals(graph.getValue(0, 0), clonedGraph.getValue(0, 0));
		assertFalse(graph.equals(clonedGraph));
	}

}
