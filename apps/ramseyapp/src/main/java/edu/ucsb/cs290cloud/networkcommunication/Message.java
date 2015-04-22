package edu.ucsb.cs290cloud.networkcommunication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -186320699428979457L;
	private String message;
	private String strategy;
	private int[][] graph;
	private int bestCount;
	private int bestCountTime;
	private int nbTimesBestCount;	
	
	public int[][] getGraph() {
		return graph;
	}
	public void setGraph(int[][] graph) {
		this.graph = graph;
	}
	public String getStrategy() {
		return strategy;
	}
	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getBestCountTime() {
		return bestCountTime;
	}
	public void setBestCountTime(int bestCountTime) {
		this.bestCountTime = bestCountTime;
	}
	public int getBestCount() {
		return bestCount;
	}
	public void setBestCount(int bestCount) {
		this.bestCount = bestCount;
	}
	public int getNbTimesBestCount() {
		return nbTimesBestCount;
	}
	public void setNbTimesBestCount(int nbTimesBestCount) {
		this.nbTimesBestCount = nbTimesBestCount;
	}
	
	public byte[] serialize() {
		ByteArrayOutputStream bos;
		ObjectOutput out;
		
		bos = new ByteArrayOutputStream();
		out = null;
		
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(this);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	    return bos.toByteArray();
	}
	
	public static Message deserialize(byte[] receivedBytes) {
		ObjectInputStream iStream;
		Message receivedMessage;
		
		receivedMessage = null;
		
		try {
			iStream = new ObjectInputStream(new ByteArrayInputStream(receivedBytes));
			receivedMessage = (Message) iStream.readObject();
			iStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		return receivedMessage;
	}
}
