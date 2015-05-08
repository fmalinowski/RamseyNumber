package edu.ucsb.cs290cloud.server.graphdao;

import edu.ucsb.cs290cloud.standalone.Graph;
import org.parse4j.Parse;
import org.parse4j.ParseException;
import org.parse4j.ParseObject;
import org.parse4j.ParseQuery;
import org.parse4j.util.ParseRegistry;

import java.util.List;

/**
 * Created by ethan_000 on 5/7/2015.
 * https://github.com/thiagolocatelli/parse4j
 */
public class GraphDaoParse implements GraphDao {
    private String APP_ID = "PsF2vq4N7bmLY4dfJ5SZltOuqFM69yS5Y9sg9GIF";
    private String APP_REST_API_ID = "aqJJVZQvihcvAQqE8QqNGOnELKEUkbTxFneF5zFL";

    private final String GRAPH_CLASSNAME= "graph";
    private final String DATA_COLUMN= "data";
    private final String SIZE_COLUMN= "size";

    public GraphDaoParse(){
        Parse.initialize(APP_ID, APP_REST_API_ID);
    }
    public void storeGraph(Graph graph){
        int size = graph.size();
        ParseObject o = new ParseObject(GRAPH_CLASSNAME);
        o.put(SIZE_COLUMN,size);
        o.put(DATA_COLUMN, graph.getGraphDataString());

        try {
            o.save();
        }catch (ParseException e)
        {
            e.printStackTrace();
        }

    }
    public Graph getLatestGraph(){
        ParseQuery<ParseObject> q = ParseQuery.getQuery(GRAPH_CLASSNAME);
        q.orderByDescending(SIZE_COLUMN);
        q.limit(1);
        try{
            List<ParseObject> l = q.find();
            ParseObject o = l.get(0);
            System.out.print(o.getString(DATA_COLUMN));
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }


}
