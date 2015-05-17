package edu.ucsb.cs290cloud.commons;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class Message implements Serializable {
	
	private static final long serialVersionUID = -186320699428979457L;
	private String message;
	private GraphWithInfos graphWithInfos;
	private Class strategyClass;
	private HashMap<String, String> strategyParameters = new HashMap<String, String>();
	
//	private int numberOfMatrixSplits; // Only for strategy1Distributed
//	private int clientPositionInMatrixSplit; // Only for strategy1Distributed
	
	public GraphWithInfos getGraph() {
		return graphWithInfos;
	}
	public void setGraph(GraphWithInfos graphWithInfos) {
		this.graphWithInfos = graphWithInfos;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Class getStrategyClass() {
		return strategyClass;
	}
	public void setStrategyClass(Class strategyClass) {
		this.strategyClass = strategyClass;
	}
//	public int getNumberOfMatrixSplits() {
//		return numberOfMatrixSplits;
//	}
//	public void setNumberOfMatrixSplits(int numberOfMatrixSplits) {
//		this.numberOfMatrixSplits = numberOfMatrixSplits;
//	}	
//	public int getClientPositionInMatrixSplit() {
//		return clientPositionInMatrixSplit;
//	}
//	public void setClientPositionInMatrixSplit(int clientPositionInMatrixSplit) {
//		this.clientPositionInMatrixSplit = clientPositionInMatrixSplit;
//	}
	
	public HashMap<String, String> getStrategyParameters() {
		return strategyParameters;
	}
	public void setStrategyParameters(HashMap<String, String> strategyParameters) {
		this.strategyParameters = strategyParameters;
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
