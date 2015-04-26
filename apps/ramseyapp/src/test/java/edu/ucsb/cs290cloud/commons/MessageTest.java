package edu.ucsb.cs290cloud.commons;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.ucsb.cs290cloud.commons.GraphWithInfos;
import edu.ucsb.cs290cloud.commons.Message;
import edu.ucsb.cs290cloud.server.ServerResponder;

public class MessageTest {

	@Test
	public void testSerializeAndDeserialize() {
		Message message, receivedMessage;
		GraphWithInfos graphWithInfos, receivedGraphWithInfos;
		int[][] graph, receivedGraph;
		
		byte[] bytesToSend;
		
		graph = new int[2][2];
		graph[0][0] = 2343456;
		graph[0][1] = 2345;
		graph[1][0] = 345;
		graph[1][1] = 89;
		
		graphWithInfos = new GraphWithInfos(graph);
		graphWithInfos.setBestCount(3);
		graphWithInfos.setGraphID(123456);
		graphWithInfos.setParentGraphID(123455);
		graphWithInfos.setStatus(GraphWithInfos.Status.COUNTER_EXAMPLE_FOUND_BUT_NOT_SAVED);
		graphWithInfos.setStrategyUsed("HANG OVER STRATEGY");
		graphWithInfos.setTimeSpentOnBestCount(100000000);
		
		message = new Message();
		message.setMessage("hello I'm here!!!");
		message.setGraph(graphWithInfos);
		
		bytesToSend = message.serialize();
		
		assertNotNull(bytesToSend);
		assert(bytesToSend.length > 0);
		
		receivedMessage = Message.deserialize(bytesToSend);
		
		assertNotNull(receivedMessage);
		assertEquals("hello I'm here!!!", receivedMessage.getMessage());
		receivedGraphWithInfos = receivedMessage.getGraph();
		assertEquals(3, receivedGraphWithInfos.getBestCount());
		assertEquals(123456, receivedGraphWithInfos.getGraphID());
		assertEquals(123455, receivedGraphWithInfos.getParentGraphID());
		assertEquals(GraphWithInfos.Status.COUNTER_EXAMPLE_FOUND_BUT_NOT_SAVED, receivedGraphWithInfos.getStatus());
		assertEquals("HANG OVER STRATEGY", receivedGraphWithInfos.getStrategyUsed());
		assertEquals(100000000, receivedGraphWithInfos.getTimeSpentOnBestCount());
		
		receivedGraph = receivedGraphWithInfos.getRawGraph(); 
		assertEquals(2343456, receivedGraph[0][0]);
		assertEquals(2345, receivedGraph[0][1]);
		assertEquals(345, receivedGraph[1][0]);
		assertEquals(89, receivedGraph[1][1]);
	}
	
}
