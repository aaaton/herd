package se.lth.cs.hajenner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by antonsodergren on 2/22/16.
 * Mention is a detected mention in the text, and keeps a list of all solrIDs for this mention
 */
public class Mention {
    private int start, end;
    private String text;
    private List<Candidate> candidates, originalCandidates;
    public Mention(int start, int end, String matchText, List<Candidate> candidates) {
        this.start = start;
        this.end = end;
        this.text = matchText;
        this.candidates = candidates;
        this.originalCandidates = candidates;
    }

    public Mention(int start, int end, String matchText) {
        this(start,end,matchText, new ArrayList<>());
    }

    public Mention(Mention m) {
        this.start = m.start;
        this.end = m.end;
        this.text = m.text;
        this.candidates = m.candidates;
        this.originalCandidates = m.originalCandidates;
    }

    public String getText() {
        return text;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getLength() {
        return end - start;
    }



    public void addCountToNode(Candidate n) {
        for(Candidate node: candidates) {
            if (node.getQID().equals(n.getQID())) {
                node.setCount(node.getCount()+n.getCount());
            }
        }
    }


    public boolean hasCandidate(Candidate candidate) {
        for (Candidate c : candidates) {
            if ( c.getQID().equals(candidate.getQID())) {
                return true;
            }
        }
        return false;
    }

    public List<Candidate> getOriginalCandidates() { return originalCandidates; }


    public void addCandidate(Candidate n) {
        candidates.add(n);
    }
    public List<Candidate> getCandidates(){
        return candidates;
    }

    public void sortCandidates() {
        boolean swapped = true;
        while (swapped) {
            swapped = false;
            for (int i = 1; i< candidates.size(); i++) {
                if(candidates.get(i).getWeight() > candidates.get(i-1).getWeight()) {
                    swapped = true;
                    Candidate n = candidates.get(i-1);
                    candidates.remove(i-1);
                    candidates.add(i,n);
                }
            }
        }
    }

    public boolean equals(Object o) {
        if (o instanceof Mention) {
            Mention s = (Mention)o;
            return s.start == start && s.end == end && s.text.equals(text);
        }
        return false;
    }

    public boolean overlaps(Mention m) {
        return (m.start >= start && m.start <= end) || (m.end >= start && m.end <= end) || containedBy(m);
    }
    public boolean containedBy(Mention m) {
        return (m.start <= start && m.end > end || m.start < start && m.end >= end);
    }

    public boolean isLongerOrRight(Mention m) {
        return this.getLength() > m.getLength() || (this.getLength() == m.getLength() && this.getEnd() > m.getEnd());
    }

    public Mention mergeWith(Mention n) {
        Mention newMention = new Mention(n);
        //Candidates won't be right. Doesn't matter

        boolean found = false;
        newMention.candidates = new ArrayList<>();
        for(Candidate mode: n.candidates) {
            for (int i = 0; i < candidates.size(); i++) {
                Candidate node = candidates.get(i);
                if(mode.getQID().equals(node.getQID())) {
                    newMention.candidates.add(new Candidate(node.getSurfaceForm(),node.getQID(),node.getTotal(),node.getCount()+mode.getCount()));
                    candidates.remove(i);
                    found = true;
                    break;
                }
            }
            if(!found) {
                newMention.candidates.add(mode);
            }
        }
        for (Candidate node : candidates) {
            newMention.candidates.add(node);
        }
        newMention.sortCandidates();
        return newMention;
    }

    public String toString() {
        return getStart()+"->"+getEnd()+":"+getText();
    }


    public void setCandidates(ArrayList<Candidate> candidates) {
        this.candidates = candidates;
    }
}
