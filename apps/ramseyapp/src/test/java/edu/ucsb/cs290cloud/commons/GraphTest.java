package edu.ucsb.cs290cloud.commons;

import edu.ucsb.cs290cloud.commons.Graph;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * Created by ethan_000 on 5/1/2015.
 */
public class GraphTest {
    int s1,s2,s3;
    int[][] d1,d2,d3;

    @Before
    public void initialize()
    {
        s1 = 4;
        d1 = new int[s1][s1];
        d1[0][3] = 1;
        d1[1][2] = 1;
        d1[1][0] = 1;

        s2 = 4;
        d2 = new int[s2][s2];
        d2[0][3] = 1;
        d2[1][2] = 1;
        d2[1][0] = 1;
        d2[2][0] = 1;

        s3 = 5;
        d3 = new int[s3][s3];
        d3[0][3] = 1;
        d3[1][2] = 1;
        d3[1][0] = 1;
    }

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

    @Test
    public void testGraphFromString()
    {
        Graph g1 = new Graph(d1);
        String graphString = g1.getGraphDataString();
        Graph graphFromString = new Graph(graphString, s1);
        assertEquals(true,g1.equals(graphFromString));
    }

    @Test
    public void testEquals()
    {
        Graph g1 = new Graph(d1);

        assertEquals(false, g1.equals(null)); //test null
        assertEquals(true, g1.equals(new Graph(d1))); //test equals on same data
        assertEquals(false, g1.equals(new Graph(d3)));//test equals on different size
        assertEquals(false,g1.equals(new Graph(d2)));//test equals on same size and different data

    }
}
