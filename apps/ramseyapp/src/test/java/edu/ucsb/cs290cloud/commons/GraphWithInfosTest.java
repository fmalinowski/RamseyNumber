package edu.ucsb.cs290cloud.commons;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import edu.ucsb.cs290cloud.ramseychecker.CliqueCounter;
import edu.ucsb.cs290cloud.server.Scheduler;

@RunWith(PowerMockRunner.class)
@PrepareForTest({GraphWithInfos.class}) // Class that creates the new instance of GraphsExplorer and that we want to mock
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
	
	@Test
	public void testIsCounterExample_counterExampleCase() {
		CliqueCounter cliqueCounterMock;
		GraphWithInfos graph;
		
		cliqueCounterMock = PowerMock.createMock(CliqueCounter.class);
		try {
			PowerMock.expectNew(CliqueCounter.class, new Class[] {int[][].class}, EasyMock.anyObject(int[][].class)).andReturn(cliqueCounterMock);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		cliqueCounterMock.getMonochromaticSubcliquesCount();
		PowerMock.expectLastCall().andReturn(0);
		
		PowerMock.replayAll();
		
		graph = new GraphWithInfos(2);
		assertTrue(graph.isCounterExample());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testIsCounterExample_notCounterExampleCase() {
		CliqueCounter cliqueCounterMock;
		GraphWithInfos graph;
		
		cliqueCounterMock = PowerMock.createMock(CliqueCounter.class);
		try {
			PowerMock.expectNew(CliqueCounter.class, new Class[] {int[][].class}, EasyMock.anyObject(int[][].class)).andReturn(cliqueCounterMock);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		cliqueCounterMock.getMonochromaticSubcliquesCount();
		PowerMock.expectLastCall().andReturn(1);
		
		PowerMock.replayAll();
		
		graph = new GraphWithInfos(2);
		assertFalse(graph.isCounterExample());
		
		PowerMock.verifyAll();
	}
}
