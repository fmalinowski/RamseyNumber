package edu.ucsb.cs290cloud.server.graphdao;


import static org.junit.Assert.*;

import edu.ucsb.cs290cloud.standalone.Graph;
import org.junit.Test;

/**
 * Created by ethan_000 on 5/7/2015.
 */
public class GraphDaoParseTest {
    @Test
    public void testSave()
    {
        GraphDaoParse parse = new GraphDaoParse();
        Graph g = new Graph(3);
        parse.storeGraph(g);
    }

    @Test
    public void testGetLatestGraph()
    {
        System.out.println("Test getGraph");
        GraphDaoParse parse = new GraphDaoParse();
        parse.getLatestGraph();
    }
}

