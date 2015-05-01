package edu.ucsb.cs290cloud.standalone;

import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Created by ethan_000 on 5/1/2015.
 */
public class GraphTest {
    @Test
    public void testGetSizeUpperTriangle() {
        assertEquals(1,new Graph(2).getSizeUpperTriangle());    //2=1  1
        assertEquals(3,new Graph(3).getSizeUpperTriangle());    //3=3  2+1
        assertEquals(6,new Graph(4).getSizeUpperTriangle());    //4=6  3+2+1
        assertEquals(10,new Graph(5).getSizeUpperTriangle());   //5=10 4+3+2+1
        assertEquals(15,new Graph(6).getSizeUpperTriangle());   //6=15 5+4+3+2+1
        assertEquals(21,new Graph(7).getSizeUpperTriangle());   //7=21 6+5+4+3+2+1
        assertEquals(28,new Graph(8).getSizeUpperTriangle());   //8=28 7+6+5+4+3+2+1
    }
}
