package edu.ucsb.cs290cloud.networkcommunication;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.ucsb.cs290cloud.server.ServerResponder;

public class MessageTest {

	@Test
	public void testSerializeAndDeserialize() {
		Message message, receivedMessage;
		int[][] graph, receivedGraph;;
		
		byte[] bytesToSend;
		
		graph = new int[2][2];
		graph[0][0] = 2343456;
		graph[0][1] = 2345;
		graph[1][0] = 345;
		graph[1][1] = 89;
		
		message = new Message();
		message.setMessage("hello I'm here!!!");
		message.setStrategy("Strategy 1");
		message.setGraph(graph);
		
		bytesToSend = message.serialize();
		
		assertNotNull(bytesToSend);
		assert(bytesToSend.length > 0);
		
		receivedMessage = Message.deserialize(bytesToSend);
		
		assertNotNull(receivedMessage);
		assertEquals("hello I'm here!!!", receivedMessage.getMessage());
		assertEquals("Strategy 1", receivedMessage.getStrategy());
		receivedGraph = receivedMessage.getGraph();
		assertEquals(2343456, receivedGraph[0][0]);
		assertEquals(2345, receivedGraph[0][1]);
		assertEquals(345, receivedGraph[1][0]);
		assertEquals(89, receivedGraph[1][1]);
	}
	
}
