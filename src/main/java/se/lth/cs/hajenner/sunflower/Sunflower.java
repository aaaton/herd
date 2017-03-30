package se.lth.cs.hajenner.sunflower;

import se.lth.cs.hajenner.Candidate;
import se.lth.cs.hajenner.Mention;
import se.lth.cs.hajenner.sunflower.model.Category;
import se.lth.cs.hajenner.sunflower.model.Resources;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by antonsodergren on 5/19/16.
 */
public class Sunflower {
    private LookFor lookFor;
    private final int width = 3;
    private final int depth = 4;
    public Sunflower() {
        Resources resources = new Resources();
        lookFor = new LookFor(resources);
    }

    public TopicCentroid createTopicCentroid(List<Mention> mentions) {
        TopicCentroid tc = new TopicCentroid();
        for (Mention mention : mentions) {
            Candidate c = mention.getCandidates().get(0);
            tc.linearCombination(lookFor.findCategoryDepth(c.getQNumber(), width, depth));
        }
        return tc;
    }

    public ArrayList<Mention> pruneByCutoff(TopicCentroid tc, ArrayList<Mention> mentions, double cutoff) {
        ArrayList<Mention> newMentions = new ArrayList<>();
        for (Mention mention : mentions) {
//            Candidate c = mention.getCandidates().get(0);
            for (Candidate c : mention.getCandidates()) {
                List<Category> categories = lookFor.findCategoryDepth(c.getQNumber(), width, depth);
                double similarity = tc.similarityTo(categories);
                if (similarity > cutoff) {
                    Mention m = new Mention(mention.getStart(), mention.getEnd(), mention.getText());
                    m.addCandidate(c);
                    newMentions.add(m);
                    c.setSimilarity(similarity);
                    break;
                }
            }
        }
        return newMentions;
    }

    public ArrayList<Mention> refineTopicCentroid(TopicCentroid tc, ArrayList<Mention> mentions) {
        int size = mentions.size();
        double cutoff = 0.6;

        while(true) {
            double leastSimilar = 1.0;
            int indexOfLeastSimilar = -1;
            int i = 0;
            for (Mention mention : mentions) {
                Candidate c = mention.getCandidates().get(0);
                List<Category> categories = lookFor.findCategoryDepth(c.getQNumber(), width, depth);
                double similarity = tc.similarityTo(categories);
                if (similarity < leastSimilar) {
                    leastSimilar = similarity;
                    indexOfLeastSimilar = i;
                }
                i++;
            }

            if(indexOfLeastSimilar < 0 || leastSimilar > cutoff) {
                break;
            }

            if(indexOfLeastSimilar > -1) {
                mentions.remove(indexOfLeastSimilar);
            }

            if (((double)mentions.size() / size) <= 0.5) {
                break;
            }

        }

        return mentions;
    }

}
