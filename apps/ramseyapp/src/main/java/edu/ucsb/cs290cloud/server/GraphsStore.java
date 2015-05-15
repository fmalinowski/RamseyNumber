package edu.ucsb.cs290cloud.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;

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
	
	public HashMap<Integer, LinkedList<GraphWithInfos>> loadCounterExamples() {
		HashMap<Integer, LinkedList<GraphWithInfos>> counterExamples;
		LinkedList<GraphWithInfos> graphsList;
		int currentGraphSize;
		
		counterExamples = new HashMap<Integer, LinkedList<GraphWithInfos>>();
		
		File[] folders = this.counterExamplesFolder.listFiles(new FilenameFilter() {
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		
		for (File folder : folders) {
			currentGraphSize = Integer.parseInt(folder.getName());
			File[] filesInFolder = folder.listFiles();
			
			graphsList = new LinkedList<GraphWithInfos>();
			counterExamples.put(currentGraphSize, graphsList);
			
			this.loadGraphsOfSizeInMemory(graphsList, filesInFolder, currentGraphSize);
		}
		
		return counterExamples;
	}
	
	protected void loadGraphsOfSizeInMemory(LinkedList<GraphWithInfos> graphsList, 
			File[] graphFiles, int currentGraphSize) {
		GraphWithInfos graph;
		String graphString;
		
		for (File graphFile : graphFiles) {
			graphString = this.readFile(graphFile);
			graph = new GraphWithInfos(graphString, currentGraphSize);
			
			if (graphsList.size() < GraphsExplorer.MAX_NUMBER_OF_GRAPHS_STORED_IN_MEMORY_PER_SIZE) {
				graphsList.add(graph);
			}
		}
	}
	
	protected String readFile(File file) {
		String fileContent, line;
		
		fileContent = "";
		
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getAbsolutePath()));
			
			while ((line = bufferedReader.readLine()) != null) {
				fileContent += line;
				fileContent += "\n";
			}
			
			bufferedReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Error while reading line from the Tweet posts file");
			e.printStackTrace();
		}
		
		return fileContent;
	}
}
