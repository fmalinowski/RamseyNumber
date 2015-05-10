package edu.ucsb.cs290cloud.commons;

import java.io.Serializable;
import java.util.Random;

public class Graph implements Serializable {
	
	protected int[][] graph;
	protected int size;
	
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

	public Graph(String graphString, int size)
	{
		this.size = size;
		graph = new int[size][size];
		int i,j;
		i=j=0;
		for(int k=0; k<graphString.length();k++ )
		{
			switch (graphString.charAt(k))
			{
				case '1':
					graph[i][j] = 1;
					j++;
					break;
				case '0':
					graph[i][j] = 0;
					j++;
					break;
				case '\n':
					i++;
					j=0;
					break;
				default:
					break;
			}
		}
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
		output += getGraphDataString();
		return output;
	}

	public String getGraphDataString() {
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < this.size; i++) {
			for (int j = 0; j < this.size; j++) {
				output.append(this.graph[i][j] + " ");
			}
			output.append("\n");
		}

		return output.toString();
	}
	
	// Copy the old graph into the new one and randomize the last rows and last columns
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


	public Pair<Integer,Integer> getRandomCoordInUpperTriangle()
	{
		int n = getSizeUpperTriangle();
		Random rn = new Random();
		int r = rn.nextInt(n);
		//translate random number to i,j
		int i,j;
		i = 0;
		j = 1;
		while(r > 0)
		{
			j++;
			if(j >= size)
			{
				i++;
				j=i+1;
			}
			r--;
		}
		return new Pair<Integer,Integer>(i,j);
	}

	/**
	 * n=(n*(n+1))/2 but n needs to be n-1
	 * @return
	 */
	protected int getSizeUpperTriangle()
	{
		return (size-1)*(size)/2;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Graph))
			return false;
		if (obj == this)
			return true;

		Graph compare = (Graph) obj;
		if(size != compare.size())
			return false;

		for(int i=0;i<size;i++)
		{
			for(int j=0;j<size;j++)
			{
				if(graph[i][j] != compare.getValue(i,j))
					return false;
			}
		}
		return true;
	}
}
