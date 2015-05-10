package edu.ucsb.cs290cloud.server;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Test;

import edu.ucsb.cs290cloud.commons.GraphWithInfos;

public class GraphsExplorerTest {

	@Test
	public void testAddGraphToListAndKeepBestCounts() {
		GraphsExplorer graphsExplorer;
		LinkedList<GraphWithInfos> graphsList;
		GraphWithInfos graph1, graph2, graph3, graph4, graph5, graph6;
		
		graph1 = new GraphWithInfos(2);
		graph2 = new GraphWithInfos(2);
		graph3 = new GraphWithInfos(2);
		graph4 = new GraphWithInfos(2);
		graph5 = new GraphWithInfos(2);
		graph6 = new GraphWithInfos(2);
		
		graph1.setBestCount(4);
		graph2.setBestCount(6);
		graph3.setBestCount(3);
		graph4.setBestCount(5);
		graph5.setBestCount(1);
		graph6.setBestCount(2);
		graphsList = new LinkedList<GraphWithInfos>();
		
		graphsExplorer = new GraphsExplorer();		
		
		graphsExplorer.addGraphBeingComputedToListAndKeepBestCounts(graphsList, graph1);
		assertEquals(4, graphsList.get(0).getBestCount());
		
		graphsExplorer.addGraphBeingComputedToListAndKeepBestCounts(graphsList, graph2);
		assertEquals(4, graphsList.get(0).getBestCount());
		assertEquals(6, graphsList.get(1).getBestCount());
		
		graphsExplorer.addGraphBeingComputedToListAndKeepBestCounts(graphsList, graph3);
		assertEquals(3, graphsList.get(0).getBestCount());
		assertEquals(4, graphsList.get(1).getBestCount());
		assertEquals(6, graphsList.get(2).getBestCount());
		
		graphsExplorer.addGraphBeingComputedToListAndKeepBestCounts(graphsList, graph4);
		assertEquals(3, graphsList.get(0).getBestCount());
		assertEquals(4, graphsList.get(1).getBestCount());
		assertEquals(5, graphsList.get(2).getBestCount());
		assertEquals(6, graphsList.get(3).getBestCount());
		
		graphsExplorer.addGraphBeingComputedToListAndKeepBestCounts(graphsList, graph5);
		assertEquals(1, graphsList.get(0).getBestCount());
		assertEquals(3, graphsList.get(1).getBestCount());
		assertEquals(4, graphsList.get(2).getBestCount());
		assertEquals(5, graphsList.get(3).getBestCount());
		assertEquals(6, graphsList.get(4).getBestCount());
		
		graphsExplorer.addGraphBeingComputedToListAndKeepBestCounts(graphsList, graph6);
		assertEquals(5, graphsList.size());
		assertEquals(1, graphsList.get(0).getBestCount());
		assertEquals(2, graphsList.get(1).getBestCount());
		assertEquals(3, graphsList.get(2).getBestCount());
		assertEquals(4, graphsList.get(3).getBestCount());
		assertEquals(5, graphsList.get(4).getBestCount());
	}
	
	@Test
	public void testAddGraphToListAndKeepBestCounts_whenMultipleBestGraphs() {
		GraphsExplorer graphsExplorer;
		LinkedList<GraphWithInfos> graphsList;
		GraphWithInfos graph1, graph2, graph3, graph4, graph5, graph6;
		
		graph1 = new GraphWithInfos(2);
		graph2 = new GraphWithInfos(2);
		graph3 = new GraphWithInfos(2);
		graph4 = new GraphWithInfos(2);
		graph5 = new GraphWithInfos(2);
		graph6 = new GraphWithInfos(2);
		
		graph1.setBestCount(2);
		graph2.setBestCount(2);
		graph3.setBestCount(2);
		graphsList = new LinkedList<GraphWithInfos>();
		
		graphsExplorer = new GraphsExplorer();		
		
		graphsExplorer.addGraphBeingComputedToListAndKeepBestCounts(graphsList, graph1);
		assertEquals(graph1, graphsList.get(0));
		
		graphsExplorer.addGraphBeingComputedToListAndKeepBestCounts(graphsList, graph2);
		assertEquals(graph1, graphsList.get(0));
		assertEquals(graph2, graphsList.get(1));
		
		graphsExplorer.addGraphBeingComputedToListAndKeepBestCounts(graphsList, graph3);
		assertEquals(graph1, graphsList.get(0));
		assertEquals(graph2, graphsList.get(1));
		assertEquals(graph3, graphsList.get(2));
	}
	
	@Test
	public void testAddCounterExampleToList() {
		GraphsExplorer graphsExplorer;
		LinkedList<GraphWithInfos> graphsList;
		GraphWithInfos graph1, graph2, graph3, graph4, graph5, graph6;
		
		graph1 = new GraphWithInfos(2);
		graph2 = new GraphWithInfos(2);
		graph3 = new GraphWithInfos(2);
		graph4 = new GraphWithInfos(2);
		graph5 = new GraphWithInfos(2);
		graph6 = new GraphWithInfos(2);
		
		graphsList = new LinkedList<GraphWithInfos>();
		
		graphsExplorer = new GraphsExplorer();		
		
		graphsExplorer.addCounterExampleToList(graphsList, graph1);
		assertEquals(graph1, graphsList.get(0));
		
		graphsExplorer.addGraphBeingComputedToListAndKeepBestCounts(graphsList, graph2);
		assertEquals(graph1, graphsList.get(0));
		assertEquals(graph2, graphsList.get(1));
		
		graphsExplorer.addGraphBeingComputedToListAndKeepBestCounts(graphsList, graph3);
		assertEquals(graph1, graphsList.get(0));
		assertEquals(graph2, graphsList.get(1));
		assertEquals(graph3, graphsList.get(2));
		
		graphsExplorer.addGraphBeingComputedToListAndKeepBestCounts(graphsList, graph4);
		assertEquals(graph1, graphsList.get(0));
		assertEquals(graph2, graphsList.get(1));
		assertEquals(graph3, graphsList.get(2));
		assertEquals(graph4, graphsList.get(3));
		
		graphsExplorer.addGraphBeingComputedToListAndKeepBestCounts(graphsList, graph5);
		assertEquals(graph1, graphsList.get(0));
		assertEquals(graph2, graphsList.get(1));
		assertEquals(graph3, graphsList.get(2));
		assertEquals(graph4, graphsList.get(3));
		assertEquals(graph5, graphsList.get(4));
		
		graphsExplorer.addGraphBeingComputedToListAndKeepBestCounts(graphsList, graph6);
		assertEquals(5, graphsList.size());
		assertEquals(graph1, graphsList.get(0));
		assertEquals(graph2, graphsList.get(1));
		assertEquals(graph3, graphsList.get(2));
		assertEquals(graph4, graphsList.get(3));
		assertEquals(graph5, graphsList.get(4));
	}
	
	@Test
	public void testGetMaxCounterExamplesSize() {
		GraphsExplorer graphsExplorer;
		GraphWithInfos graph1, graph2, graph3, graphBeingComputed;
		
		graph1 = new GraphWithInfos(4);
		graph2 = new GraphWithInfos(7);
		graph3 = new GraphWithInfos(5);
		graphBeingComputed = new GraphWithInfos(8);
		
		graphsExplorer = new GraphsExplorer();
		assertEquals(0, graphsExplorer.getMaxCounterExamplesSize());
		
		graphsExplorer.addNewCounterExample(graph1);
		assertEquals(4, graphsExplorer.getMaxCounterExamplesSize());
		
		graphsExplorer.addNewCounterExample(graph2);
		assertEquals(7, graphsExplorer.getMaxCounterExamplesSize());
		
		graphsExplorer.addGraphBeingComputed(graphBeingComputed);
		assertEquals(7, graphsExplorer.getMaxCounterExamplesSize());
		
		graphsExplorer.addNewCounterExample(graph3);
		assertEquals(7, graphsExplorer.getMaxCounterExamplesSize());
	}
	
	@Test
	public void testGetMaxGraphBeingComputedSize() {
		GraphsExplorer graphsExplorer;
		GraphWithInfos graph1, graph2, graph3, counterExample;
		
		graph1 = new GraphWithInfos(4);
		graph2 = new GraphWithInfos(7);
		graph3 = new GraphWithInfos(5);
		counterExample = new GraphWithInfos(8);
		
		graphsExplorer = new GraphsExplorer();
		assertEquals(0, graphsExplorer.getMaxGraphBeingComputedSize());
		
		graphsExplorer.addGraphBeingComputed(graph1);
		assertEquals(4, graphsExplorer.getMaxGraphBeingComputedSize());
		
		graphsExplorer.addGraphBeingComputed(graph2);
		assertEquals(7, graphsExplorer.getMaxGraphBeingComputedSize());
		
		graphsExplorer.addNewCounterExample(counterExample);
		assertEquals(7, graphsExplorer.getMaxGraphBeingComputedSize());
		
		graphsExplorer.addGraphBeingComputed(graph3);
		assertEquals(7, graphsExplorer.getMaxGraphBeingComputedSize());
	}
	
	@Test
	public void testGetCounterExamples() {
		GraphsExplorer graphsExplorer;
		GraphWithInfos graph0, graph1, graph2, graph3, graph4, graph5, graph6, graph7, graph8;
		LinkedList<GraphWithInfos> graphsListSize3, graphsListSize4, graphsListSize5;
		
		graph0 = new GraphWithInfos(3);
		graph1 = new GraphWithInfos(4);
		graph2 = new GraphWithInfos(4);
		graph3 = new GraphWithInfos(4);
		graph4 = new GraphWithInfos(4);
		graph5 = new GraphWithInfos(4);
		graph6 = new GraphWithInfos(4);
		graph7 = new GraphWithInfos(5);
		graph8 = new GraphWithInfos(3);
		
		graphsExplorer = new GraphsExplorer();
		
		graphsExplorer.addNewCounterExample(graph0);
		graphsListSize3 = graphsExplorer.getCounterExamples(3);
		graphsListSize4 = graphsExplorer.getCounterExamples(4);
		assertEquals(1, graphsListSize3.size());
		assertNull(graphsListSize4);
		assertEquals(graph0, graphsListSize3.get(0));
		
		graphsExplorer.addNewCounterExample(graph1);
		graphsListSize3 = graphsExplorer.getCounterExamples(3);
		graphsListSize4 = graphsExplorer.getCounterExamples(4);
		assertEquals(1, graphsListSize3.size());
		assertEquals(1, graphsListSize4.size());
		assertEquals(graph0, graphsListSize3.get(0));
		assertEquals(graph1, graphsListSize4.get(0));
		
		graphsExplorer.addNewCounterExample(graph2);
		graphsListSize3 = graphsExplorer.getCounterExamples(3);
		graphsListSize4 = graphsExplorer.getCounterExamples(4);
		assertEquals(1, graphsListSize3.size());
		assertEquals(2, graphsListSize4.size());
		assertEquals(graph0, graphsListSize3.get(0));
		assertEquals(graph1, graphsListSize4.get(0));
		assertEquals(graph2, graphsListSize4.get(1));
		
		graphsExplorer.addNewCounterExample(graph3);
		graphsListSize3 = graphsExplorer.getCounterExamples(3);
		graphsListSize4 = graphsExplorer.getCounterExamples(4);
		assertEquals(1, graphsListSize3.size());
		assertEquals(3, graphsListSize4.size());
		assertEquals(graph0, graphsListSize3.get(0));
		assertEquals(graph1, graphsListSize4.get(0));
		assertEquals(graph2, graphsListSize4.get(1));
		assertEquals(graph3, graphsListSize4.get(2));
		
		graphsExplorer.addNewCounterExample(graph4);
		graphsListSize3 = graphsExplorer.getCounterExamples(3);
		graphsListSize4 = graphsExplorer.getCounterExamples(4);
		assertEquals(1, graphsListSize3.size());
		assertEquals(4, graphsListSize4.size());
		assertEquals(graph0, graphsListSize3.get(0));
		assertEquals(graph1, graphsListSize4.get(0));
		assertEquals(graph2, graphsListSize4.get(1));
		assertEquals(graph3, graphsListSize4.get(2));
		assertEquals(graph4, graphsListSize4.get(3));
		
		graphsExplorer.addNewCounterExample(graph5);
		graphsListSize3 = graphsExplorer.getCounterExamples(3);
		graphsListSize4 = graphsExplorer.getCounterExamples(4);
		assertEquals(1, graphsListSize3.size());
		assertEquals(5, graphsListSize4.size());
		assertEquals(graph0, graphsListSize3.get(0));
		assertEquals(graph1, graphsListSize4.get(0));
		assertEquals(graph2, graphsListSize4.get(1));
		assertEquals(graph3, graphsListSize4.get(2));
		assertEquals(graph4, graphsListSize4.get(3));
		assertEquals(graph5, graphsListSize4.get(4));
		
		graphsExplorer.addNewCounterExample(graph6);
		graphsListSize3 = graphsExplorer.getCounterExamples(3);
		graphsListSize4 = graphsExplorer.getCounterExamples(4);
		graphsListSize5 = graphsExplorer.getCounterExamples(5);
		assertEquals(1, graphsListSize3.size());
		assertEquals(5, graphsListSize4.size());
		assertNull(graphsListSize5);
		assertEquals(graph0, graphsListSize3.get(0));
		assertEquals(graph1, graphsListSize4.get(0));
		assertEquals(graph2, graphsListSize4.get(1));
		assertEquals(graph3, graphsListSize4.get(2));
		assertEquals(graph4, graphsListSize4.get(3));
		assertEquals(graph5, graphsListSize4.get(4));
		
		graphsExplorer.addNewCounterExample(graph7);
		graphsListSize3 = graphsExplorer.getCounterExamples(3);
		graphsListSize4 = graphsExplorer.getCounterExamples(4);
		graphsListSize5 = graphsExplorer.getCounterExamples(5);
		assertEquals(1, graphsListSize3.size());
		assertEquals(5, graphsListSize4.size());
		assertEquals(1, graphsListSize5.size());
		assertEquals(graph0, graphsListSize3.get(0));
		assertEquals(graph1, graphsListSize4.get(0));
		assertEquals(graph2, graphsListSize4.get(1));
		assertEquals(graph3, graphsListSize4.get(2));
		assertEquals(graph4, graphsListSize4.get(3));
		assertEquals(graph5, graphsListSize4.get(4));
		assertEquals(graph7, graphsListSize5.get(0));
		
		graphsExplorer.addNewCounterExample(graph8);
		graphsListSize3 = graphsExplorer.getCounterExamples(3);
		graphsListSize4 = graphsExplorer.getCounterExamples(4);
		graphsListSize5 = graphsExplorer.getCounterExamples(5);
		assertEquals(2, graphsListSize3.size());
		assertEquals(5, graphsListSize4.size());
		assertEquals(1, graphsListSize5.size());
		assertEquals(graph0, graphsListSize3.get(0));
		assertEquals(graph8, graphsListSize3.get(1));
		assertEquals(graph1, graphsListSize4.get(0));
		assertEquals(graph2, graphsListSize4.get(1));
		assertEquals(graph3, graphsListSize4.get(2));
		assertEquals(graph4, graphsListSize4.get(3));
		assertEquals(graph5, graphsListSize4.get(4));
		assertEquals(graph7, graphsListSize5.get(0));
	}
	
	@Test
	public void testGetGraphBeingComputedWithLowestBestCount() {
		GraphsExplorer graphsExplorer;
		GraphWithInfos graph1, graph2, graph3, graph4, graph5, returnedgraph;
		
		graphsExplorer = new GraphsExplorer();
		
		graph1 = new GraphWithInfos(3);
		graph2 = new GraphWithInfos(3);
		graph3 = new GraphWithInfos(3);
		graph4 = new GraphWithInfos(3);
		graph5 = new GraphWithInfos(4);
		
		graph1.setBestCount(3);
		graph2.setBestCount(2);
		graph3.setBestCount(4);
		graph4.setBestCount(1);
		graph5.setBestCount(0);
		
		returnedgraph = graphsExplorer.getGraphBeingComputedWithLowestBestCount(3);
		assertNull(returnedgraph);
		
		graphsExplorer.addGraphBeingComputed(graph1);
		returnedgraph = graphsExplorer.getGraphBeingComputedWithLowestBestCount(3);
		assertEquals(graph1, returnedgraph);
		
		graphsExplorer.addGraphBeingComputed(graph2);
		returnedgraph = graphsExplorer.getGraphBeingComputedWithLowestBestCount(3);
		assertEquals(graph2, returnedgraph);
		
		graphsExplorer.addGraphBeingComputed(graph3);
		returnedgraph = graphsExplorer.getGraphBeingComputedWithLowestBestCount(3);
		assertEquals(graph2, returnedgraph);
		
		graphsExplorer.addGraphBeingComputed(graph4);
		returnedgraph = graphsExplorer.getGraphBeingComputedWithLowestBestCount(3);
		assertEquals(graph4, returnedgraph);
		
		returnedgraph = graphsExplorer.getGraphBeingComputedWithLowestBestCount(4);
		assertNull(returnedgraph);
		
		graphsExplorer.addGraphBeingComputed(graph5);
		returnedgraph = graphsExplorer.getGraphBeingComputedWithLowestBestCount(3);
		assertEquals(graph4, returnedgraph);
		
		returnedgraph = graphsExplorer.getGraphBeingComputedWithLowestBestCount(4);
		assertEquals(graph5, returnedgraph);
	}
}
