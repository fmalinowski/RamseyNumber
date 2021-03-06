package edu.ucsb.cs290cloud.server;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.powermock.api.easymock.PowerMock.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import edu.ucsb.cs290cloud.commons.GraphWithInfos;
import edu.ucsb.cs290cloud.commons.Message;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ServerResponder.class}) // Class that creates the new instance and that we want to mock
public class ServerResponderTest {
	
	@Test
	public void testSendMessage() {
		
		byte[] bytesToSend = {4, 3, 7};
		
		GraphsExplorer graphsExplorerMock;
		DatagramSocket serverSocketMock = PowerMock.createMock(DatagramSocket.class);
		DatagramPacket packetMock = PowerMock.createMock(DatagramPacket.class);
		
		InetAddress inetAddressMock = PowerMock.createMock(InetAddress.class);
		
		packetMock.getAddress();
		PowerMock.expectLastCall().andReturn(inetAddressMock);
		packetMock.getPort();
		PowerMock.expectLastCall().andReturn(1234);
		
		// Mock object of the class that should be constructed
		DatagramPacket responsePacket = PowerMock.createMock(DatagramPacket.class);
		// Expect a new construction of an object of type DatagramPacket.class but 
		// instead return the mock object
		
		// We need also to add @PrepareForTest({ServerResponder.class}) before the TestClass
		// because ServerResponder will instantiate a new DatagramPacket in sendMessage that
		// we want to Mock
		// We're not preparing DatagramPacket.class because a new instance of 
		// the DatagramPacket is never created when doing expectNew.
		// However we must prepare the class that does the actual new call (ServerResponder)
		try {
			PowerMock.expectNew(DatagramPacket.class, bytesToSend, bytesToSend.length, inetAddressMock, 1234).andReturn(responsePacket);
			serverSocketMock.send(responsePacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		graphsExplorerMock = PowerMock.createMock(GraphsExplorer.class);
		
		// We make the mock object effective after having declared all the mock stuff
		PowerMock.replayAll();
		
		// Test now
		ServerResponder serverResponder = new ServerResponder(serverSocketMock, packetMock, new Scheduler(graphsExplorerMock));
		serverResponder.sendMessage(bytesToSend);
		
		// Make sure all the calls were eventually done correctly
		PowerMock.verifyAll();
		
		
	}
	
	@Test
	public void testGetNewTaskForClient() {
		Scheduler schedulerMock;
		Message messageFromClient, messageFromServer;
		GraphWithInfos graphFromClient, graphFromSchedulerWhenReady,
			graphFromSchedulerWhenCounterExample, graphFromSchedulerWhenStatus;
		ServerResponder serverResponder;
		DatagramSocket serverSocketMock;
		DatagramPacket packetMock;
		
		// Create the mocks
		serverSocketMock = PowerMock.createMock(DatagramSocket.class);
		packetMock = PowerMock.createMock(DatagramPacket.class);
		schedulerMock = PowerMock.createMock(Scheduler.class);
		
		graphFromClient = new GraphWithInfos(1);
		
		// ** Case where client sends a Ready message **
		messageFromClient = new Message();
		messageFromClient.setMessage("READY");
		messageFromClient.setGraph(graphFromClient);
		
		// Expected calls and returns
		graphFromSchedulerWhenReady = new GraphWithInfos(2);
		schedulerMock.getNewTask();
		PowerMock.expectLastCall().andReturn(graphFromSchedulerWhenReady);
		PowerMock.replayAll();
		
		// Test
		serverResponder = new ServerResponder(serverSocketMock, packetMock, schedulerMock);
		messageFromServer = serverResponder.getNewTaskForClient(messageFromClient);
		
		assertEquals("NEWGRAPH", messageFromServer.getMessage());
		assertEquals(graphFromSchedulerWhenReady, messageFromServer.getGraph());
		
		// ** Case where client sends a COUNTEREXAMPLE message **
		PowerMock.reset(schedulerMock);
		messageFromClient.setMessage("COUNTEREXAMPLE");
		graphFromSchedulerWhenCounterExample = new GraphWithInfos(3);
		
		schedulerMock.processFoundCounterExample(graphFromClient);
		PowerMock.expectLastCall().andReturn(graphFromSchedulerWhenCounterExample);
		PowerMock.replay(schedulerMock);
		
		messageFromServer = serverResponder.getNewTaskForClient(messageFromClient);
		
		assertEquals("NEWGRAPH", messageFromServer.getMessage());
		assertEquals(graphFromSchedulerWhenCounterExample, messageFromServer.getGraph());
		
		// ** Case where client sends a STATUS message and server wants client to switch to 
		// another graph **
		PowerMock.reset(schedulerMock);
		messageFromClient.setMessage("STATUS");
		graphFromSchedulerWhenStatus = new GraphWithInfos(4);
				
		schedulerMock.processStatusUpdateFromClient(graphFromClient);
		PowerMock.expectLastCall().andReturn(graphFromSchedulerWhenStatus);
		PowerMock.replay(schedulerMock);
				
		messageFromServer = serverResponder.getNewTaskForClient(messageFromClient);
				
		assertEquals("NEWGRAPH", messageFromServer.getMessage());
		assertEquals(graphFromSchedulerWhenStatus, messageFromServer.getGraph());
		
		// ** Case where client sends a STATUS message and server wants client to continue **
		PowerMock.reset(schedulerMock);
		messageFromClient.setMessage("STATUS");
						
		schedulerMock.processStatusUpdateFromClient(graphFromClient);
		PowerMock.expectLastCall().andReturn(null);
		PowerMock.replay(schedulerMock);
						
		messageFromServer = serverResponder.getNewTaskForClient(messageFromClient);
						
		assertEquals("CONTINUE", messageFromServer.getMessage());
		assertEquals(null, messageFromServer.getGraph());
		
		PowerMock.verifyAll();
	}
	
	@Test
	public void testRun() {
		Scheduler schedulerMock;
		Message messageFromClient, messageFromServer;
		ServerResponder serverResponderPartialMock;
		DatagramSocket serverSocketMock;
		DatagramPacket packetMock;
		GraphWithInfos graphFromClient, graphFromServer;
		byte[] serializedMessageFromClient, serializedMessageFromServer;
		
		// Create the mocks
		serverSocketMock = PowerMock.createMock(DatagramSocket.class);
		packetMock = PowerMock.createMock(DatagramPacket.class);
		schedulerMock = PowerMock.createMock(Scheduler.class);
		
		graphFromClient = new GraphWithInfos(2);
		messageFromClient = new Message();
		messageFromClient.setMessage("WHATEVER");
		messageFromClient.setGraph(graphFromClient);
		
		serializedMessageFromClient = messageFromClient.serialize();
		
		graphFromServer = new GraphWithInfos(3);
		messageFromServer = new Message();
		messageFromServer.setMessage("RESPONSE");
		messageFromServer.setGraph(graphFromServer);
		serializedMessageFromServer = messageFromServer.serialize();
		
		// Partial mocking of ServerResponder to mock sendMessage and getNewTaskForClient
		serverResponderPartialMock = EasyMock.createMockBuilder(ServerResponder.class)
			.withConstructor(serverSocketMock, packetMock, schedulerMock)
			.addMockedMethod("getNewTaskForClient")
			.addMockedMethod("sendMessage")
			.createMock();
		
		packetMock.getData();
		PowerMock.expectLastCall().andReturn(serializedMessageFromClient);
		
		EasyMock.expect(serverResponderPartialMock.getNewTaskForClient(
				EasyMock.anyObject(Message.class))).andReturn(messageFromServer);
		
		serverResponderPartialMock.sendMessage(aryEq(serializedMessageFromServer));
		
		PowerMock.replayAll();
		EasyMock.replay(serverResponderPartialMock);
		
		serverResponderPartialMock.run();
		
		PowerMock.verifyAll();
		EasyMock.verify(serverResponderPartialMock);
	}

}
