package edu.ucsb.cs290cloud.server;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

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
}
