package edu.ucsb.cs290cloud.graphdao;


import edu.ucsb.cs290cloud.commons.Graph;

/**
 * Created by ethan_000 on 5/7/2015.
 */
public class GraphDaoParseTest {
    //@Test
    public void testSave()
    {
        GraphDaoParse parse = new GraphDaoParse();
        Graph g = new Graph(13);
        parse.storeGraph(g);
    }

    //@Test
    public void testGetLatestGraph()
    {
        GraphDaoParse parse = new GraphDaoParse();
        Graph g = parse.getLatestGraph();
        System.out.println(g.printGraph());
    }
}

