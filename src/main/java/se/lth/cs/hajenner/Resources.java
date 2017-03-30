package se.lth.cs.hajenner;

import se.lth.cs.hajenner.pagerank.PageRank;
import se.lth.cs.hajenner.lucene.LuceneCommunicator;
import se.lth.cs.hajenner.sunflower.Sunflower;

/**
 * Created by antonsodergren on 8/4/16.
 */
public class Resources {
    public static final String core = "erd";
    public static final Resources Instance = new Resources(core);
    public  final Sunflower sunflower;
    public  final PageRank pageRank;
    public static final String luceneDir = "/mnt/2_TB_HD/Solr/server/solr"; //local setup
    public static final String pagerankPath = "/mnt/2_TB_HD/pagerank.ser"; //local setup
//    private final String luceneDir = "src/main/resources/luceneCommunicator";
    public  final LuceneCommunicator luceneCommunicator;
    private Resources(String core) {
        try {
            sunflower = new Sunflower();
            pageRank = new PageRank(pagerankPath);
            luceneCommunicator = new LuceneCommunicator(luceneDir, core);
            System.out.println("LuceneCommunicator ok");
        } catch( Exception e) {
            e.printStackTrace();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {}

            throw new RuntimeException(e);
        }
    }
}
