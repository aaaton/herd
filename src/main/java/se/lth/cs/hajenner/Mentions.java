package se.lth.cs.hajenner;

import se.lth.cs.hajenner.sunflower.Sunflower;
import se.lth.cs.hajenner.sunflower.TopicCentroid;
import se.lth.cs.hajenner.pagerank.PageRank;
import java.util.*;

/**
 * Created by antonsodergren on 3/4/16.
 * Mentions keeps the list of detected hajenner in the text, and the filtering methods related to these
 */
public class Mentions {
    private final Sunflower sunflower;
    private final PageRank pr;
    private ArrayList<Mention> list, original;
    private String untagged;
    private Contextual contextual;

    public Mentions(ArrayList<Mention> mentionList, String untagged, Sunflower sunflower, PageRank pr) {
        this.list = mentionList;
        this.original = clone(list);
        this.untagged = untagged;
        contextual = new Contextual(untagged);
        this.pr = pr;
        this.sunflower = sunflower;
    }

    public void filter() {
        list = lightPruning(list);
        list = keepTopCandidates(list);
        addGenericNames(list);
        if(pr!=null) {
            pageRank(100f);
        }
        list = new DetectionExtender(this).extendMentions();
        merge(list);
        sort(list);
        longestDominantRight(list);

        if(sunflower != null) {
            //Create Topic Centroid
            TopicCentroid tc = sunflower.createTopicCentroid(list);

            //Refine TC
            ArrayList<Mention> other = sunflower.refineTopicCentroid(tc, clone(list));
            tc = sunflower.createTopicCentroid(other);
//            list = other;

            //Add from original tags.
            original = lightPruning(original);
            sort(original);
            original = sunflower.pruneByCutoff(tc, original, 0.2);
            mergeLists(list,original);
            list = new DetectionExtender(this).extendMentions();
            merge(list);
            sort(list);
            longestDominantRight(list);

            if(pr!=null)
                pageRank(300f);
        }
    }

    private ArrayList<Mention> keepTopCandidates(ArrayList<Mention> list) {
        int n = 5;
        for (Mention mention : list) {
            ArrayList<Candidate> topN = new ArrayList<>();
            for (Candidate c : mention.getCandidates()) {
                if (topN.size() == 0) {
                    topN.add(c);
                    continue;
                }

                boolean added = false;
                for (int i = 0; i < topN.size(); i++) {
                    if(topN.get(i).getCount()<c.getCount()) {
                        topN.add(i,c);
                        added = true;
                        break;
                    }
                }
                if (!added) {
                    topN.add(c);
                }

                if (topN.size()>n) {
                    topN.remove(n);
                }
            }

            mention.setCandidates(topN);
        }
        return list;
    }

    private void mergeLists(ArrayList<Mention> list, ArrayList<Mention> original) {
        for (Mention mention : original) {
            if(!list.contains(mention)) {
                list.add(mention);
            }
        }
    }


    private ArrayList<Mention> lightPruning(ArrayList<Mention> list) {
        ArrayList<Mention> filteredMatches = new ArrayList<>();
        for (Mention match: list) {
            Mention filteredMatch = new Mention(match.getStart(), match.getEnd(), match.getText());
            for (Candidate candidate: match.getCandidates()) {
                if (lightPrune(candidate)) {
                    filteredMatch.addCandidate(candidate);
                }
            }
            if (filteredMatch.getCandidates().size() > 0) {
                filteredMatches.add(filteredMatch);
            }
        }
        return filteredMatches;
    }

    private boolean lightPrune(Candidate candidate) {
        if (candidate.getProbability()<0.005)
            return false;
        return true;
    }


    public String getText() {
        return untagged;
    }

    public ArrayList<Mention> getList() {
        return list;
    }



    public HashMap<String,Candidate> hardPruning(float cutoff) {

        ArrayList<Mention> filteredMatches = new ArrayList<>();
        HashMap<String,Candidate> usedNodes = new HashMap<>();
        for (Mention match: list) {
            Mention filteredMatch = new Mention(match.getStart(), match.getEnd(), match.getText());
            for (Candidate candidate: match.getCandidates()) {
                if (candidate.getSuspicion() < cutoff) {
                    usedNodes.put(candidate.getQID(), candidate);
                    filteredMatch.addCandidate(candidate);
                }
            }
            if (filteredMatch.getCandidates().size() > 0) {
                filteredMatches.add(filteredMatch);
            }
        }
        list = filteredMatches;
        return usedNodes;
    }


    public void addGenericNames(ArrayList<Mention> list) {
        HashMap<String,Candidate> usedNodes = new HashMap<>();
        for (Mention mention : list) {
            for (Candidate candidate : mention.getCandidates()) {
                usedNodes.put(candidate.getQID(),candidate);
            }
        }

        //Generic names
        for (Mention match: original) {
            if(list.contains(match)) {
                continue;
            }
            Mention filteredMatch = new Mention(match.getStart(), match.getEnd(), match.getText());
            for (Candidate candidate: match.getCandidates()) {
                if(keepGeneric(candidate,usedNodes)) {
                    candidate.setCount(candidate.getCount());
                    filteredMatch.addCandidate(candidate);
                }
            }

            if(filteredMatch.getCandidates().size() > 0) {
                list.add(filteredMatch);
            }
        }
    }

    private void pageRank(float cutoff) {
        int windowSize = 100;
        int gap = windowSize;
        ArrayList<Candidate> candidates = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Mention m = list.get(i);
            candidates.addAll(m.getCandidates());
            if (i == gap) {
                pr.CalculateRanks(candidates);
                candidates = new ArrayList<>();
                //slide back a bit:
                i -= windowSize/2;
                gap=i+windowSize;
            }
        }
        pr.CalculateRanks(candidates);
        setSuspicion();
        hardPruning(cutoff);
    }

    private void setSuspicion() {
        contextual.pushToCandidates(list);
        for (Mention match: list) {
            for (Candidate candidate : match.getCandidates()) {
                keep(candidate, match);
            }
        }
    }

    private ArrayList<Mention> merge(ArrayList<Mention> list) {
        ArrayList<Mention> mergedList = new ArrayList<>();

        for (Mention mention : list) {
            Mention merged = new Mention(mention.getStart(),mention.getEnd(), mention.getText());
            for (Candidate candidate : mention.getCandidates()) {
                if(merged.hasCandidate(candidate)) {
                    merged.addCountToNode(candidate);
                } else {
                    merged.addCandidate(candidate);
                }
            }
            mergedList.add(merged);
        }
        return mergedList;
    }

    private ArrayList<Mention> sort(ArrayList<Mention> list) {
        for (Mention mention : list) {
            mention.sortCandidates();
        }
        return list;
    }
    
    private ArrayList<Mention> longestDominantRight( ArrayList<Mention> list) {
        for(int i = 0; i< list.size(); i++) {
            Mention main = list.get(i);
            for(int j = 0; j< list.size(); j++) {
                Mention other = list.get(j);
                if (main != other && main.overlaps(other)){
                    //Found overlap!
                    if(main.isLongerOrRight(other)) {
                        //Remove other, lower index
                        list.remove(j--);
                    } else {
                        //Remove main, lower index, skip to next
                        list.remove(i--);
                        break;
                    }
                }
            }
        }
        return list;
    }


    //All rules are experimental at the moment
    public boolean keep(Candidate c, Mention m) {
        String sf = c.getSurfaceForm();
        String text = m.getText();
        int suspicion = 0;
        int cutoff = 100;
        boolean debug = false;

        if(c.getQID().equals("")) {
            debug = true;
            System.out.println(c);
        }

        if (!sf.equals(text)) {//Casing matches
            suspicion += 150;
            if(debug)
                System.out.println("\t"+sf+" != "+text);
        }
        if (!c.looksLikeAGenericName()) {
            suspicion += 300;
            if(debug)
                System.out.println("\t"+"doesn't look like a name");
        }
        if (c.isStopwords()) {
            suspicion += 200;
            if(debug)
                System.out.println("\t"+"Is a stopword");
        }
        else if (c.isOnlywords() && c.isOneWord()) {
            suspicion += 100;
            if(debug)
                System.out.println("\t"+"one word, only regular words");
        }
        if (c.getProbability() < 0.01) {
            suspicion += 50;
        }
        if (c.getProbability() < 0.08) {
            suspicion += 100;
        }
        if (c.getProbability() > 0.5) {
            suspicion -= 100;
        }

        if (c.isGeneric()) {
            suspicion+=50;
            if(debug)
                System.out.println("\tis generic");
        }
        if (c.isInHeadline() && c.isOnlywords()) {
            suspicion += 101;
            if(debug)
                System.out.println("\t"+"Is in a headline");
        }

//        if (!c.isDefaultSense()) {
//            suspicion += 50;
//            if(debug)
//                System.out.println("\t"+"It is not default sense");
//        }
        if (c.getCount() < 15) {
            suspicion += 50;
            if(debug)
                System.out.println("\t"+"low count");
        }
        if (c.getCount() < 5) {
            suspicion+=50;
            if(debug)
                System.out.println("\t"+"super low count");
        }
        if(c.isContained()) {
            suspicion+=100;
        }

        suspicion -= sf.length()/10;

        if(debug)
            System.out.println("\t"+suspicion+" "+c.getCount());

        //System.out.println(candidate +"|"+ candidate.isCommon()+"|"+ candidate.isGeneric()+"|"+ candidate.isOnlywords()+"|"+ looksLikeName(match.getText()));
        c.setSuspicion(suspicion);
        return suspicion < cutoff;
    }

    private boolean keepGeneric(Candidate n, HashMap<String,Candidate> nodes) {
        return n.looksLikeAName() && nodes.containsKey(n.getQID());
    }

    private ArrayList<Mention> clone(List<Mention> mentionList) {
        ArrayList<Mention> clonedList = new ArrayList<>();
        for (Mention mention : mentionList) {
            clonedList.add(new Mention(mention));
        }
        return clonedList;
    }

}
