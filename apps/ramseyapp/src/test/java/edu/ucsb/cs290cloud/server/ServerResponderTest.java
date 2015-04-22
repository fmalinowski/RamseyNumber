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

import edu.ucsb.cs290cloud.networkcommunication.Message;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ServerResponder.class}) // Class that creates the new instance and that we want to mock
public class ServerResponderTest {

//	@Test
//	public void testReceiveAndSendMessage() {	
////		fail("Not yet implemented");
//	}
	
	@Test
	public void testSendMessage() {
		
		byte[] bytesToSend = {4, 3, 7};
		
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
		
		// We make the mock object effective after having declared all the mock stuff
		PowerMock.replayAll();
		
		// Test now
		ServerResponder serverResponder = new ServerResponder(serverSocketMock, packetMock, new Scheduler());
		serverResponder.sendMessage(bytesToSend);
		
		// Make sure all the calls were eventually done correctly
		PowerMock.verifyAll();
		
		
	}

}
