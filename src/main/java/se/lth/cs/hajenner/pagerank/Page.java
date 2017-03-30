package se.lth.cs.hajenner.pagerank;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import se.lth.cs.hajenner.Candidate;

import java.util.ArrayList;

/**
 * Created by antonsodergren on 4/26/16.
 */
public class Page {
    private final double initialScore;
    private Candidate candidate;
    private int link;
    private IntArrayList links;
    private double score, prevScore;
    private double damp = 0.85f;
    private int divisor = 1;

    public Page(Candidate candidate, double initialScore) {
        this.candidate = candidate;
        this.initialScore = initialScore;
        this.prevScore = initialScore;
        this.score = initialScore;
        this.links = new IntArrayList();

        link = Integer.parseInt(candidate.getQID().substring(1));

    }

    public void addLink(int link) {
        if(!links.contains(link)) {
            links.add(link);
        }
    }

    public IntArrayList getLinks() {
        return links;
    }

    public double handOut() {
        return prevScore/divisor;
    }
    public void setDivisor(Int2ObjectOpenHashMap<ArrayList<Page>> candidates) {
        int numlinks = 0;
        for (Integer link : links) {
            numlinks += candidates.get(link.intValue()).size();
        }
        divisor = numlinks;
    }
    public void updateScore(Int2ObjectOpenHashMap<ArrayList<Page>> candidates) {
        this.score = initialScore*(1-damp);
        for (Integer link : links) {
            for (Page page : candidates.get(link)) {
                double selfDamp = 1;
                if(page.candidate.getQID().equals(candidate.getQID()))
                    selfDamp = 0.5;
                score += selfDamp*damp*page.handOut();
            }
        }
    }

    public double getScore() {
        return score;
    }

    public double getScoreChange() {
        if(links.size()<=1) {
            return 0.1;
        }
        return getScore()/initialScore;
    }

    public boolean is(Candidate c) {
        return c.exactEquals(candidate);
    }

    public void iterationDone() {
        prevScore = score;
    }
}
