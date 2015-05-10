package edu.ucsb.cs290cloud.server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import edu.ucsb.cs290cloud.commons.GraphWithInfos;

public class GraphsStore {
	
	private File rootFolder;
	private File counterExamplesFolder;
	private File graphsBeingComputedFolder;
	
	public GraphsStore(String rootFolder) throws Exception {
		this.rootFolder = new File(rootFolder);
		
		if (!this.rootFolder.exists()) {
			throw new Exception("The root folder where the graphs should be stored does not exist! The graphs will not be stored on disks");
		}
		else {
			this.counterExamplesFolder = new File(this.rootFolder, "CounterExamples");
			this.graphsBeingComputedFolder = new File(this.rootFolder, "GraphsBeingComputed");
			
			if (!this.counterExamplesFolder.exists()) {
				this.counterExamplesFolder.mkdir();
			}
			if (!this.graphsBeingComputedFolder.exists()) {
				this.graphsBeingComputedFolder.mkdir();
			}
		}
	}
	
	public void storeCounterExample(GraphWithInfos graph) {
		this.storeGraph(this.counterExamplesFolder, graph);
	}
	
	public void storeGraphBeingComputed(GraphWithInfos graph) {
		// NOT SURE IF IT SHOULD BE IMPLEMENTED
	}

	public String getNextFileNameForGraph(File folder) {	
		File[] listOfFiles = folder.listFiles();
		return Integer.toString(listOfFiles.length);
	}
	
	public void printToFile(File file, String graphString) {
		PrintWriter pWriter;
		
		try {
			pWriter = new PrintWriter(new FileWriter(file, true));
			pWriter.print(graphString);
			pWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void storeGraph(File folder, GraphWithInfos graph) {
		File graphFolder;
		String printedGraph, fileName;
		
		graphFolder = new File(folder, Integer.toString(graph.size()));
		if (!graphFolder.exists()) {
			graphFolder.mkdir();
		}
		
		fileName = this.getNextFileNameForGraph(graphFolder);
		printedGraph = graph.printGraph();
		
		this.printToFile(new File(graphFolder, fileName), printedGraph);
	}
}
