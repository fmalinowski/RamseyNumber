package edu.ucsb.cs290cloud.server;

import static org.junit.Assert.*;

import java.net.DatagramPacket;
import java.util.LinkedList;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import edu.ucsb.cs290cloud.commons.GraphWithInfos;
import edu.ucsb.cs290cloud.commons.Message;
import edu.ucsb.cs290cloud.standalone.GraphFactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Scheduler.class}) // Class that creates the new instance of GraphsExplorer and that we want to mock
public class SchedulerTest {

	@Test
	public void testGetNewTask_whenLaunchingMasterNode() {
		GraphsExplorer graphsExplorerMock;
		Scheduler scheduler;
		GraphWithInfos graphWithInfos;
		
		// CONFIGURE THE MOCK
		graphsExplorerMock = PowerMock.createMock(GraphsExplorer.class);
		try {
			PowerMock.expectNew(GraphsExplorer.class).andReturn(graphsExplorerMock);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		graphsExplorerMock.getMaxCounterExamplesSize();
		PowerMock.expectLastCall().andReturn(0);
		
		graphsExplorerMock.getMaxGraphBeingComputedSize();
		PowerMock.expectLastCall().andReturn(0);
		
		PowerMock.replayAll();
		
		// TEST
		scheduler = new Scheduler();
		graphWithInfos = scheduler.getNewTask();
		
		assertEquals(Scheduler.INITIAL_GENERATED_GRAPH_SIZE, graphWithInfos.size());
		
		// MAKE SURE ALL THE EXPECTED CALLS WERE MADE
		PowerMock.verifyAll();
	}
	
	@Test
	public void testGetNewTask_whenNoGraphOfHigherSizeAreBeingComputed() {
		GraphsExplorer graphsExplorerMock;
		Scheduler scheduler;
		GraphWithInfos counterExample1, counterExample2, counterExample3, answerGraph;
		LinkedList<GraphWithInfos> listOfCounterExamples;
		
		// CONFIGURE THE MOCK
		graphsExplorerMock = PowerMock.createMock(GraphsExplorer.class);
		try {
			PowerMock.expectNew(GraphsExplorer.class).andReturn(graphsExplorerMock);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		graphsExplorerMock.getMaxCounterExamplesSize();
		PowerMock.expectLastCall().andReturn(26);
		
		graphsExplorerMock.getMaxGraphBeingComputedSize();
		PowerMock.expectLastCall().andReturn(26);
		
		counterExample1 = GraphFactory.generateRandomGraph(26);
		counterExample2 = GraphFactory.generateRandomGraph(26);
		counterExample3 = GraphFactory.generateRandomGraph(26);
		
		listOfCounterExamples = new LinkedList<GraphWithInfos>();
		listOfCounterExamples.add(counterExample1);
		listOfCounterExamples.add(counterExample2);
		listOfCounterExamples.add(counterExample3);
		
		graphsExplorerMock.getCounterExamples(26);
		PowerMock.expectLastCall().andReturn(listOfCounterExamples);
		
		PowerMock.replayAll();
		
		// TEST
		scheduler = new Scheduler();
		answerGraph = scheduler.getNewTask();
		
		// We make sure that the new graph contains the counterExample and is one size above
		assertEquals(27, answerGraph.size());
		
		for (int i = 0; i < 26; i++) {
			for (int j = 0; j < 26; j++) {
				assertEquals(counterExample1.getValue(i, j), answerGraph.getValue(i, j));
			}
		}
		
		// MAKE SURE ALL THE EXPECTED CALLS WERE MADE
		PowerMock.verifyAll();
	}
	
	@Test
	public void testGetNewTask_whenGraphOfHigherSizeBeingComputedExists() {
		GraphsExplorer graphsExplorerMock;
		Scheduler scheduler;
		GraphWithInfos graphBeingComputed, answerGraph;
		
		// CONFIGURE THE MOCK
		graphsExplorerMock = PowerMock.createMock(GraphsExplorer.class);
		try {
			PowerMock.expectNew(GraphsExplorer.class).andReturn(graphsExplorerMock);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		graphsExplorerMock.getMaxCounterExamplesSize();
		PowerMock.expectLastCall().andReturn(26);
		
		graphsExplorerMock.getMaxGraphBeingComputedSize();
		PowerMock.expectLastCall().andReturn(27);
		
		graphBeingComputed = new GraphWithInfos(27);
		
		graphsExplorerMock.getGraphBeingComputedWithLowestBestCount(27);
		PowerMock.expectLastCall().andReturn(graphBeingComputed);
		
		PowerMock.replayAll();
		
		// TEST
		scheduler = new Scheduler();
		answerGraph = scheduler.getNewTask();
		
		// We make sure that the new graph contains the counterExample and is one size above
		assertEquals(27, answerGraph.size());
		assertEquals(graphBeingComputed, answerGraph);
		
		// MAKE SURE ALL THE EXPECTED CALLS WERE MADE
		PowerMock.verifyAll();
	}
	
	@Test
	public void testProcessFoundCounterExample_whenNoGraphOfHigherSizeAreBeingComputed() {
		GraphsExplorer graphsExplorerMock;
		Scheduler scheduler;
		GraphWithInfos graphFromClient, counterExample1, counterExample2, answerGraph;
		LinkedList<GraphWithInfos> listOfCounterExamples;
		
		graphFromClient = GraphFactory.generateRandomGraph(4);
		
		// CONFIGURE THE MOCK
		graphsExplorerMock = PowerMock.createMock(GraphsExplorer.class);
		try {
			PowerMock.expectNew(GraphsExplorer.class).andReturn(graphsExplorerMock);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		graphsExplorerMock.addNewCounterExample(graphFromClient);
		
		graphsExplorerMock.getMaxCounterExamplesSize();
		PowerMock.expectLastCall().andReturn(4);
		
		graphsExplorerMock.getMaxGraphBeingComputedSize();
		PowerMock.expectLastCall().andReturn(0);
		
		counterExample1 = GraphFactory.generateRandomGraph(4);
		counterExample2 = GraphFactory.generateRandomGraph(4);
		
		listOfCounterExamples = new LinkedList<GraphWithInfos>();
		listOfCounterExamples.add(counterExample1);
		listOfCounterExamples.add(counterExample2);
		listOfCounterExamples.add(graphFromClient);
		
		graphsExplorerMock.getCounterExamples(4);
		PowerMock.expectLastCall().andReturn(listOfCounterExamples);
		
		PowerMock.replayAll();
		
		// TEST
		scheduler = new Scheduler();
		answerGraph = scheduler.processFoundCounterExample(graphFromClient);
		
		// We make sure that the new graph contains the counterExample and is one size above
		assertEquals(5, answerGraph.size());
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				assertEquals(counterExample1.getValue(i, j), answerGraph.getValue(i, j));
			}
		}
		
		// MAKE SURE ALL THE EXPECTED CALLS WERE MADE
		PowerMock.verifyAll();
	}
	
	@Test
	public void testprocessFoundCounterExample_whenGraphOfHigherSizeBeingComputedExists() {
		GraphsExplorer graphsExplorerMock;
		Scheduler scheduler;
		GraphWithInfos graphFromClient, graphBeingComputed, answerGraph;
		
		graphFromClient = GraphFactory.generateRandomGraph(4);
		
		// CONFIGURE THE MOCK
		graphsExplorerMock = PowerMock.createMock(GraphsExplorer.class);
		try {
			PowerMock.expectNew(GraphsExplorer.class).andReturn(graphsExplorerMock);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		graphsExplorerMock.addNewCounterExample(graphFromClient);
		
		graphsExplorerMock.getMaxCounterExamplesSize();
		PowerMock.expectLastCall().andReturn(4);
		
		graphsExplorerMock.getMaxGraphBeingComputedSize();
		PowerMock.expectLastCall().andReturn(6);
		
		graphBeingComputed = new GraphWithInfos(6);
		
		graphsExplorerMock.getGraphBeingComputedWithLowestBestCount(6);
		PowerMock.expectLastCall().andReturn(graphBeingComputed);
		
		PowerMock.replayAll();
		
		// TEST
		scheduler = new Scheduler();
		answerGraph = scheduler.processFoundCounterExample(graphFromClient);
		
		// We make sure that the new graph contains the counterExample and is one size above
		assertEquals(6, answerGraph.size());
		assertEquals(graphBeingComputed, answerGraph);
		
		// MAKE SURE ALL THE EXPECTED CALLS WERE MADE
		PowerMock.verifyAll();
	}

}
