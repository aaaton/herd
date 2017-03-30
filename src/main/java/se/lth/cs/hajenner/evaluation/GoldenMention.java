package se.lth.cs.hajenner.evaluation;

import se.lth.cs.hajenner.Mention;

/**
 * Created by antonsodergren on 5/9/16.
 */
public class GoldenMention extends Mention {
    private String qNum;
    public GoldenMention(int start, int end, String word, String qNum) {
        super(start,end,word);
        this.qNum = qNum;
    }

    public String getQNum() {
        return qNum;
    }
}
