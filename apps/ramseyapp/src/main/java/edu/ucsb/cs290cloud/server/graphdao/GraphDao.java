package edu.ucsb.cs290cloud.server.graphdao;

import edu.ucsb.cs290cloud.commons.Graph;

/**
 * Created by ethan_000 on 5/7/2015.
 */
public interface GraphDao {
    void storeGraph(Graph graph);
    Graph getLatestGraph();
}
