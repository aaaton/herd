package se.lth.cs.hajenner;

import se.lth.cs.hajenner.pagerank.PageRank;
import se.lth.cs.hajenner.lucene.LuceneCommunicator;
import se.lth.cs.hajenner.sunflower.Sunflower;

import java.util.ArrayList;

/**
 * Created by antonsodergren on 5/10/16.
 */
public class HERD {
    private String language;

    public HERD(String language) {
        this.language = language;
    }

    public Mentions tag(String text, LuceneCommunicator luceneCommunicator, Sunflower sunflower, PageRank pr) {
        ArrayList<Mention> mentionList = luceneCommunicator.queryAndParse(text);
//        SolrQuery solrQuery = new SolrQuery(text, language);
//        SolrParser parser = new SolrParser(text, solrQuery.send());
//        ArrayList<Mention> mentionList = parser.getMentions();
        Mentions mentions = new Mentions(mentionList, text, sunflower, pr);
        mentions.filter();
        return mentions;
    }
}
