package edu.ucsb.cs290cloud.standalone;

import java.io.Serializable;
import java.util.Random;

public class Graph implements Serializable {
	
	private int[][] graph;
	private int size;
	
	public Graph(int size) {
		this.size = size;
		this.graph = new int[size][size];
	}

	/**
	 * this function only checks the first array for length.
	 * It is not going to check every row for the length.
	 * For the moment it will assume that the input is properly formatted, but we might change that later.
	 * @param graph
	 */
	public Graph(int [][] graph)
	{
		size = graph.length;
		this.graph = graph;
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
		this.graph[i][j] = 1 - this.graph[i][j];
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

		Random rn = new Random();
		for (int i = 0; i < this.size; i++) {
			
			for (int j = 0; j < this.size; j++) {
				newGraph.graph[i][j] = this.graph[i][j];
			}

			int s = rn.nextInt(2);
			newGraph.graph[i][this.size()] = s;
		}

		return newGraph;
	}
	
	public int getGraphID() {
		return this.graph.hashCode();
	}
}
