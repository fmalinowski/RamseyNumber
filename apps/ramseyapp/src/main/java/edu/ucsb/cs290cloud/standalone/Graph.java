package edu.ucsb.cs290cloud.standalone;

public class Graph {
	
	private int[][] graph;
	private int size;
	
	public Graph(int size) {
		this.size = size;
		this.graph = new int[size][size];
	}
	
	public int size() {
		return this.size;
	}

	public int getValue(int i, int j) {
		return this.graph[i][j];
	}
	
	public void setValue(int i, int j, int value) {
		this.graph[i][j] = value;
	}
	
	public void flipValue(int i, int j) {
		if (i >=0 && j >= 0 && i < this.size && j < this.size) {
			this.graph[i][j] = 1 - this.graph[i][j];
		} 
	}
	
	public int[][] getRawGraph() {
		return this.graph;
	}
	
	public String printGraph() {
		String output = "Size: " + this.size + "\n";
		
		for (int i = 0; i < this.size; i++) {
			for (int j = 0; j < this.size; j++) {
				output += this.graph[i][j] + " ";
			}
			output += "\n";
		}
		
		return output;
	}
	
	// Copy the old graph into the new one and leave empty the last rows and last columns
	public Graph copyGraph(int newGraphSize) {
		Graph newGraph;
		
		if (newGraphSize < this.size) {
			System.err.println("ERROR: the new graph must be at least the same size as teh current graph.");
			return null;
		}
		
		newGraph = new Graph(newGraphSize);
		
		for (int i = 0; i < this.size; i++) {
			for (int j = 0; j < this.size; j++) {
				newGraph.graph[i][j] = this.graph[i][j];
			}
		}
		
		
		
		return newGraph;
	}
}
