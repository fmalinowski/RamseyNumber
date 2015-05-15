package edu.ucsb.cs290cloud.graphdao;

import edu.ucsb.cs290cloud.commons.GraphWithInfos;

/**
 * Created by ethan_000 on 5/7/2015.
 */
public interface GraphDao {
    void storeGraph(GraphWithInfos graph);
    GraphWithInfos getLatestGraph();
}
