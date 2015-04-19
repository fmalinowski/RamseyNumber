package edu.ucsb.cs290cloud.standalone;

import java.util.Random;

/**
 * Created by ethan_000 on 4/18/2015.
 */
public class GraphFactory {
    public static Graph generateRandomGraph(int size)
    {
        Random rn = new Random();
        int[][] graph = new int[size][size];
        for(int i=0; i<size; i++)
        {
            for(int j=i+1; j<size; j++)
            {
                graph[i][j] = rn.nextInt(2);
            }
        }
        return new Graph(graph);
    }
}
