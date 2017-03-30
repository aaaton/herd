package se.lth.cs.hajenner;

import java.util.ArrayList;

/**
 * Created by antonsodergren on 4/5/16.
 */
public class CollidingTags {
    private Mentions mentions;

    public CollidingTags(Mentions mentions) {
        this.mentions = mentions;
        removeNeighbourTags();
    }

    public void removeNeighbourTags() {
        ArrayList<Mention> list = mentions.getList();
        for (int i = 0; i < list.size(); i++) {
            Mention mention = list.get(i);
            for (int i1 = 0; i1 < list.size(); i1++) {
                Mention mention1 = list.get(i1);
                if(i!=i1) {
                    if(mention.getEnd()+1 == mention1.getStart()) {
                        for (Candidate candidate : mention.getOriginalCandidates()) {
                            if (candidate.isGeneric()) {
                                System.out.println(mention.getText());
                                list.remove(mention);
                                break;
                            }
                        }
                        for (Candidate candidate : mention1.getOriginalCandidates()) {
                            if (candidate.isGeneric()) {
                                System.out.println(mention1.getText());
                                list.remove(mention1);
                                break;
                            }
                        }
                        //list.remove(mention1);
//                        list.remove(mention);
                        break;
                    }
                }
            }
        }
    }
}
