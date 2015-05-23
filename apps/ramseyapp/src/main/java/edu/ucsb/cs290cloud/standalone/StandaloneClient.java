package edu.ucsb.cs290cloud.standalone;

import edu.ucsb.cs290cloud.commons.GraphFactory;
import edu.ucsb.cs290cloud.graphdao.GraphDao;
import edu.ucsb.cs290cloud.graphdao.GraphDaoParse;
import edu.ucsb.cs290cloud.strategies.Strategy;
import edu.ucsb.cs290cloud.strategies.Strategy2Random;

/**
 * Created by ethan_000 on 5/15/2015.
 */
public class StandaloneClient {
    public static void main(String[] args)
    {
        Strategy s = new Strategy2Random();
        s.setInitialGraph(GraphFactory.generateRandomGraph(Integer.parseInt(args[0])));
        s.run();
        GraphDao dao = new GraphDaoParse();
        if(s.getStrategyStatus()== Strategy.Status.COUNTER_EXAMPLE)
        {
            dao.storeGraph(s.getGraph());
        }

    }
}
