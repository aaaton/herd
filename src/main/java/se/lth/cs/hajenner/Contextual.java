package se.lth.cs.hajenner;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by antonsodergren on 4/5/16.
 * Provides contextual clues to a mention
 */
public class Contextual {
    private final Pattern dotPattern;
    private final Pattern newlinePattern;
    private final Pattern uppercasePattern;
    private Pattern namePattern;
    private ArrayList<Mention> matches;
    private String text;
    private String[] lines;

    public Contextual(String text) {
        this.namePattern = Pattern.compile("( \\p{Lu}\\p{L}+){2,}");
        this.text = text;
        lines = text.split("\n");

        dotPattern = Pattern.compile("^([\\.?!:] )");
        newlinePattern = Pattern.compile("^[\n\r\"]");
        uppercasePattern = Pattern.compile("(\\b\\p{Lu}\\p{L}*\\b)");

        matches = new ArrayList<>();
        findNames();
    }

    public void pushToCandidates(ArrayList<Mention> list) {
        //Calculates all the stuff once per mention and pushes it to each candidate
        for (Mention mention : list) {
            boolean isContained = isContainedByName(mention);
            boolean isInHeadline = isInHeadline(mention);
            boolean beginningOfSentence = isBeginningOfSentence(mention);
            int capitalLetters = capitalLetters(mention.getText());
            for (Candidate c : mention.getCandidates()) {
                c.setContained(isContained);
                c.setInHeadline(isInHeadline);
                c.setBeginningOfSentence(beginningOfSentence);
                c.setCapitalLetters(capitalLetters);
            }
        }
    }

    private void findNames() {

        //Matches sevaral words starting with a capital letter only separated by space.
        //++++++++++++------------------------++++++++++++++---------------------------
        // David Pamba along with team skipper Collins Obuya and Tikolo was brought in.

        Matcher matcher = namePattern.matcher(text);
        while(matcher.find()) {
            String match = matcher.group().substring(1);
            int start = matcher.start()+1; //+1 to remove initial space.
            int end = matcher.end();
            matches.add(new Mention(start,end,match));
        }
    }

    public boolean isContainedByName(Mention m) {
        for (Mention name: matches) {
            if (m.containedBy(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInHeadline(Mention match) {
        String line = getFullLineFromMatch(match);
        int words = numberOfWords(line);
        int count = capitalizedWords(line);
        float wordsCapitalizedInHeadline = 0.75f;
        return ((float)count)/(float)words > wordsCapitalizedInHeadline;
    }

    private int numberOfWords(String line) {
        int words = 0;
        int lastIndex = 0;
        while(lastIndex >= 0) {
            lastIndex = line.indexOf(" ", lastIndex+1);
            words++;
        }
        return words;
    }

    private int capitalizedWords(String text) {
        Matcher  matcher = uppercasePattern.matcher(text);
        int nbrOfCaptialLetters = 0;
        while (matcher.find())
            nbrOfCaptialLetters++;
        return nbrOfCaptialLetters;
    }

    private String getFullLineFromMatch(Mention match) {
        int total = 0;
        for(String line: lines) {
            total += line.length();
            if(total - line.length() <= match.getStart() && match.getStart() <= total) {
                return line;
            }
        }
        return "";
    }

    public boolean isBeginningOfSentence(Mention match) {
        int start = match.getStart();
        //TODO: use getFullLineFromMatch instead
        if (start <= 2) { //is beginning of document
            return true;
        } else {
            //Setup end
            int end = start+10;
            if (end > text.length()){
                end = text.length();
            }

            //Look for dots
            String sub = text.substring(start-2,end);
            Matcher m = dotPattern.matcher(sub);
            boolean beginning = m.find();

            //Look for newlines and Citation marks
            sub = sub.substring(1);
            m = newlinePattern.matcher(sub);
            boolean newLine = m.find();


            return beginning || newLine;
        }
    }

    public int capitalLetters(String text) {
        Pattern pattern = Pattern.compile("\\p{Lu}");
        Matcher  matcher = pattern.matcher(text);
        int nbrOfCaptialLetters = 0;
        while (matcher.find())
            nbrOfCaptialLetters++;
        return nbrOfCaptialLetters;
    }
}
