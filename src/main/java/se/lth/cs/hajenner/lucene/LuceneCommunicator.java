package se.lth.cs.hajenner.lucene;
import org.opensextant.solrtexttagger.LuceneTextTagger;
import org.opensextant.solrtexttagger.NamedList;
import se.lth.cs.hajenner.Candidate;
import se.lth.cs.hajenner.Mention;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by antonsodergren on 7/18/16.
 */
public class LuceneCommunicator {
    private String directory;
    private LuceneTextTagger luceneTextTagger;

    public LuceneCommunicator(String path, String core) throws Exception {
        System.out.println("Setting up Lucene");
        directory = path;
        luceneTextTagger = new LuceneTextTagger(directory+"/"+core+"/data/index");
        System.out.println("Lucene setup ok");
    }

    public ArrayList<Mention> queryAndParse(String text) {
        try {
            NamedList response = luceneTextTagger.handleRequest(text);
            return parse(response, text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    private ArrayList<Mention> parse(NamedList response, String text) {
        HashMap<Integer,Candidate> lookup = parseCandidates(response);
        return parseMentions(response,lookup, text);
    }

    private HashMap<Integer, Candidate> parseCandidates(NamedList namedList) {
        List<NamedList> response = (List)namedList.get("response");
        HashMap<Integer,Candidate> nodes = new HashMap<>();
        for (NamedList doc : response) {
            String mention = (String) doc.get("mention");
            String qid = (String) doc.get("qid");
            int count = Integer.parseInt(doc.get("count").toString());
            int total = Integer.parseInt(doc.get("total").toString());
            float probability = 1f;
            if (doc.contains("probability"))
                probability = Float.parseFloat(doc.get("probability").toString());
            boolean stopwords = doc.contains("stopwords");
            boolean onlywords = doc.contains("onlywords");
            boolean generic = doc.contains("generic");
            boolean common = doc.contains("common");
            boolean lowercase = doc.contains("lowercase");
            Integer solrID = (Integer) doc.get("id");
            Candidate c = new Candidate(solrID, mention,qid,total,count,probability,stopwords,onlywords,generic,common,lowercase);
            nodes.put(solrID,c);
        }
        return nodes;
    }
    private ArrayList<Mention> parseMentions(NamedList namedList, HashMap<Integer,Candidate> lookup, String text) {
        List<NamedList> arr = (List<NamedList>) namedList.get("tags");
        ArrayList<Mention> matches = new ArrayList<>();
        for (NamedList e : arr) {
            int start = Integer.parseInt(e.get("startOffset").toString());
            int end = Integer.parseInt(e.get("endOffset").toString());

            String matchText = text.substring(start,end);
            List<Candidate> candidates = new ArrayList<>();
            for (Integer id : ((List<Integer>) e.get("ids"))) {
                Candidate c = lookup.get(id);
                if(c!=null)
                    candidates.add(new Candidate(c));
                else
                    System.out.println(c);
            }
            matches.add(new Mention(start,end,matchText,candidates));
        }
        return matches;
    }

}
