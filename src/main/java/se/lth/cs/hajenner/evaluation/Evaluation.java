package se.lth.cs.hajenner.evaluation;

import se.lth.cs.hajenner.Candidate;
import se.lth.cs.hajenner.Mention;
import se.lth.cs.hajenner.Mentions;

import java.util.List;

/**
 * Created by antonsodergren on 3/7/16.
 */
public class Evaluation {
    private int match, disambiguated, goldenSize, mentionsSize, dgSize,dmSize;
    private float precision,recall,fScore, pD,rD,fD;
    public Evaluation(Mentions mentions, List<GoldenMention> golden) {
        goldenSize = golden.size();
        mentionsSize = mentions.getList().size();
        dgSize = goldenSize;
        dmSize = mentionsSize;
        for(Mention mention: mentions.getList()) {
            for(GoldenMention gold: golden) {
                if(mention.overlaps(gold)) {

                    List<Candidate> candidates = mention.getCandidates();
                    String mostCommon = candidates.get(0).getQID();

                    String qNum = gold.getQNum();
                    if(qNum.equals("-")) {
                        dgSize--;
                        dmSize--;
                    }

                    if(mostCommon.equals(qNum)) {
                        disambiguated += 1;
                    }

                    match = match + 1;

                    golden.remove(golden.indexOf(gold));
                    break;
                }
            }
        }

        precision = ((float)match)/mentionsSize;
        recall = ((float)match)/goldenSize;
        fScore = 2*precision*recall/(precision+recall);

        pD = ((float)disambiguated)/dmSize;
        rD = ((float)disambiguated)/dgSize;
        fD = 2*pD*rD/(pD+rD);
    }

    public String getPrecision() {
        return String.format("%.2f", precision);
    }
    public String getRecall() {
        return String.format("%.2f", recall);
    }
    public String getFScore() {
        return String.format("%.2f", fScore);
    }
    public String getPrecisionDisambiguated() {
        return String.format("%.2f", pD);
    }
    public String getRecallDisambiguated() {
        return String.format("%.2f", rD);
    }
    public String getFScoreDisambiguated() {
        return String.format("%.2f", fD);
    }
    public int getMatch() {
        return match;
    }
    public int getDisambiguated() { return disambiguated; }
    public int getGoldenSize() {
        return goldenSize;
    }
    public int getMentionsSize() {
        return mentionsSize;
    }
    public int getDisambiguatedGoldenSize() { return dgSize;}
    public int getDisambiguatedMentionsSize() { return dmSize;}
}
