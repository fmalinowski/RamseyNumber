package edu.ucsb.cs290cloud.server;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.ucsb.cs290cloud.commons.GraphWithInfos;

public class GraphsStoreTest {
	
	// FYI: Temporary folders are deleted after test has failed/passed
	@Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

	@Test(expected=Exception.class)
	public void testInstanciate_withExceptionRaised() throws Exception {
		GraphsStore graphsStore;
		
		new GraphsStore("The fuck this path doesn't exist bro!");		
	}
	
	@Test
	public void testInstanciate_whenChildFoldersDoNotExist() {
		GraphsStore graphsStore;
		
		assertFalse(new File(testFolder.getRoot(), "CounterExamples").exists());
		assertFalse(new File(testFolder.getRoot(), "GraphsBeingComputed").exists());
		
		try {
			new GraphsStore(testFolder.getRoot().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertTrue(new File(testFolder.getRoot(), "CounterExamples").exists());
		assertTrue(new File(testFolder.getRoot(), "GraphsBeingComputed").exists());
	}

	@Test
	public void testInstanciate_whenChildFoldersExist() {
		GraphsStore graphsStore;
		File tmpCounterExampleFolder, tmpGraphsBeingComputedFolder;
		File tmpCounterExample, tmpGraphBeingComputed;
		File counterExamplesFolder, graphsBeingComputedFolder;
		
		tmpCounterExample = null;
		tmpGraphBeingComputed = null;
		
		try {
			tmpCounterExampleFolder = testFolder.newFolder("CounterExamples");
			tmpGraphsBeingComputedFolder = testFolder.newFolder("GraphsBeingComputed");
			
			tmpCounterExample = tmpCounterExampleFolder.createTempFile("whateverFile", ".slut", tmpCounterExampleFolder);
			tmpGraphBeingComputed = tmpGraphsBeingComputedFolder.createTempFile("whateverFile", ".slut", tmpGraphsBeingComputedFolder);
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		counterExamplesFolder = new File(testFolder.getRoot(), "CounterExamples");
		graphsBeingComputedFolder = new File(testFolder.getRoot(), "GraphsBeingComputed");
		assertTrue(counterExamplesFolder.exists());
		assertTrue(graphsBeingComputedFolder.exists());
		
		assertTrue(new File(counterExamplesFolder, tmpCounterExample.getName()).exists());
		assertTrue(new File(graphsBeingComputedFolder, tmpGraphBeingComputed.getName()).exists());
		
		try {
			new GraphsStore(testFolder.getRoot().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertTrue(counterExamplesFolder.exists());
		assertTrue(graphsBeingComputedFolder.exists());
		assertTrue(new File(counterExamplesFolder, tmpCounterExample.getName()).exists());
		assertTrue(new File(graphsBeingComputedFolder, tmpGraphBeingComputed.getName()).exists());
	}
	
	@Test
	public void testGetNextFileNameForGraph() {
		GraphsStore graphsStore = null;
		
		File counterExampleFolder = new File(testFolder.getRoot(), "CounterExamples");
		
		try {
			graphsStore = new GraphsStore(testFolder.getRoot().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertEquals("0", graphsStore.getNextFileNameForGraph(counterExampleFolder));
		
		try {
			counterExampleFolder.createTempFile("asdf", "adf", counterExampleFolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		assertEquals("1", graphsStore.getNextFileNameForGraph(counterExampleFolder));
		
		try {
			counterExampleFolder.createTempFile("asdf", "adf", counterExampleFolder);
			counterExampleFolder.createTempFile("asdf", "adf", counterExampleFolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		assertEquals("3", graphsStore.getNextFileNameForGraph(counterExampleFolder));
	}
	
	@Test
	public void testReadFile() {
		File file;
		GraphsStore graphsStore;
		String fileContent;
		GraphWithInfos graph;
		
		graphsStore = null;
		try {
			graphsStore = new GraphsStore(testFolder.getRoot().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		file = new File(this.getClass().getResource("/edu/ucsb/cs290cloud/server/graphSize2").getFile());
		fileContent = graphsStore.readFile(file);
		System.out.println(fileContent);
		
		assertEquals("1 1 \n0 1 \n", fileContent);
		
//		graph = new GraphWithInfos(fileContent, 2);
//		assertEquals(1, graph.getValue(0,0));
//		assertEquals(1, graph.getValue(0,1));
//		assertEquals(0, graph.getValue(1,0));
//		assertEquals(1, graph.getValue(1,1));
	}
	
	@Test
	public void testLoadGraphsOfSizeInMemory() {
		GraphsStore graphsStore;
		LinkedList<GraphWithInfos> graphsList;
		File folder;
		GraphWithInfos graph1, graph2, graph3, graph4, graph5;
		
		folder = new File(this.getClass().getResource("/edu/ucsb/cs290cloud/server/test_load_graphs_of_size_in_memory").getFile());
		File[] filesInFolder = folder.listFiles();
		assertEquals(6, filesInFolder.length); // This is just to make sure the test folder did not change
		
		graphsList = new LinkedList<GraphWithInfos>();
		
		graphsStore = null;
		try {
			graphsStore = new GraphsStore(testFolder.getRoot().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		graph1 = new GraphWithInfos(new int[][]{{1,1,0},{0,1,1},{1,0,1}});
		graph2 = new GraphWithInfos(new int[][]{{1,1,0},{1,1,1},{1,0,1}});
		graph3 = new GraphWithInfos(new int[][]{{0,1,0},{0,1,1},{1,0,0}});
		graph4 = new GraphWithInfos(new int[][]{{1,1,1},{1,1,1},{1,0,1}});
		graph5 = new GraphWithInfos(new int[][]{{0,0,0},{0,1,0},{0,0,0}});
		
		graphsStore.loadGraphsOfSizeInMemory(graphsList, filesInFolder, 3);
		
		assertEquals(GraphsExplorer.MAX_NUMBER_OF_GRAPHS_STORED_IN_MEMORY_PER_SIZE, graphsList.size());
		assertTrue(graph1.equals(graphsList.get(0)));
		assertTrue(graph2.equals(graphsList.get(1)));
		assertTrue(graph3.equals(graphsList.get(2)));
		assertTrue(graph4.equals(graphsList.get(3)));
		assertTrue(graph5.equals(graphsList.get(4)));
	}
}
