package edu.ucsb.cs290cloud.commons;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Message implements Serializable {
	
	private static final long serialVersionUID = -186320699428979457L;
	private String message;
	private GraphWithInfos graphWithInfos;
	private Class strategyClass;
	
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
	public Class getStrategyClass() {
		return strategyClass;
	}
	public void setStrategyClass(Class strategyClass) {
		this.strategyClass = strategyClass;
	}
}
