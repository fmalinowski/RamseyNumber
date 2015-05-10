package edu.ucsb.cs290cloud.commons;

import java.util.LinkedList;
import java.util.TreeMap;

public class FIFOEdge {
	private static final int DEFAULT_FIFO_MAX_SIZE = 500;
	private int maxFIFOSize;
	private LinkedList fifo;
	private TreeMap redBlackIndexTree; // To find quickly presence of edges in FIFO
	
	public FIFOEdge() {
		this(DEFAULT_FIFO_MAX_SIZE);
	}
	
	public FIFOEdge(int size) {
		fifo = new LinkedList<Integer>();
		redBlackIndexTree = new TreeMap<Integer, Boolean>();
		this.maxFIFOSize = size;
	}
	
	public void insertEdge(int i, int j) {
		int keyForTree, node;
		
		keyForTree = this.getDistanceForEdge(i, j);
		
		// If fifo size bigger than max size, we throw away the head of the fifo
		if (this.fifo.size() >= this.maxFIFOSize) {
			node = (Integer) this.fifo.pop();
			this.redBlackIndexTree.remove(node);
		}
		
		this.fifo.add(keyForTree);
		this.redBlackIndexTree.put(keyForTree, true);
	}
	
	public Boolean findEdge(int i, int j) {
		int keyForTree = this.getDistanceForEdge(i, j);
		
		if (this.redBlackIndexTree.containsKey(keyForTree)) {
			return true;
		}
		return false;
	}
	
	public void resetFIFO() {
		this.fifo.clear();
		this.redBlackIndexTree.clear();
	};
	
	private int getDistanceForEdge(int i, int j) {
		return (i << 16) | j;
	}

}
