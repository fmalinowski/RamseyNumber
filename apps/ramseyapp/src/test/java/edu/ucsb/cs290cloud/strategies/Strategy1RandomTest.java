package edu.ucsb.cs290cloud.strategies;


import static org.junit.Assert.*;

import edu.ucsb.cs290cloud.commons.GraphFactory;
import org.junit.Test;

/**
 * Created by ethan_000 on 5/10/2015.
 */
public class Strategy1RandomTest {
    @Test
    public void runStrategy(){
        Strategy s = new Strategy1Random();
        s.setInitialGraph(GraphFactory.generateRandomGraph(90));
        s.run();
    }
}
