package se.lth.cs.hajenner;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;

import java.util.*;

/**
 * Created by antonsodergren on 4/5/16.
 */
public class DetectionExtender {
    private int cutoff = 100;
    private ArrayList<Mention> list;
    private String untagged;
    private Contextual contextual;
    public DetectionExtender(Mentions mentions) {
        list = mentions.getList();
        untagged = mentions.getText();
        contextual = new Contextual(untagged);
    }
    public ArrayList<Mention> extendMentions() {
        //These "mentions" have no position in the text.
        // They are merely a collection of surface forms + QIDs that might exist in the text
        HashMap<String, Mention> mentionsLookup = createAcronymsAndParts();
        Collection<Emit> emits = tagText(mentionsLookup);
        //Now they have a position in the text
        ArrayList<Mention> detectedMentions = createMentionsFromEmits(emits,mentionsLookup);
        list = mergeMentions(detectedMentions, mentionsLookup);
        return list;
    }

    public ArrayList<Mention> mergeMentions(ArrayList<Mention> detectedMentions, HashMap<String, Mention> mentionsLookup) {
        ArrayList<Mention> newList = new ArrayList<>();
        //Merge original and extended or add original mention if no merge necessary
        for(Mention m: list) {
            if(mentionsLookup.containsKey(m.getText())) {
                boolean added = false;
                for(int i = 0; i<detectedMentions.size();i++) {
                    Mention n = detectedMentions.get(i);
                    if (m.getStart()==n.getStart() && m.getEnd() == n.getEnd()) {
                        newList.add(m.mergeWith(n));
                        detectedMentions.remove(i);
                        added = true;
                        break;
                    }
                }
                if(!added) {
                    newList.add(m);
                }
            } else {
                newList.add(m);
            }
        }
        //Add remaining extended mentions
        newList.addAll(detectedMentions);
        return newList;
    }


    public ArrayList<Mention> createMentionsFromEmits(Collection<Emit> emits, HashMap<String,Mention> mentionsLookup) {
        //Create list of mentions that are found in the text
        ArrayList<Mention> detectedMentions = new ArrayList<>();
        for(Emit emit:emits) {
            Mention m = mentionsLookup.get(emit.getKeyword());
            m.setStart(emit.getStart());
            m.setEnd(emit.getEnd()+1);
            Mention mention = new Mention(m.getStart(),m.getEnd(),m.getText());
            
            boolean kept = false;
            calculateSuspicion(m);
            for(Candidate c: m.getCandidates()) {
                if(c.getSuspicion() < cutoff) {
                    mention.addCandidate(c);
                    kept = true;
                }
            }
            if(kept)
                detectedMentions.add(mention);
        }
        return detectedMentions;
    }

    private void calculateSuspicion(Mention mention) {
        int capitalLetters = contextual.capitalLetters(mention.getText());
        boolean beginningOfSentence = contextual.isBeginningOfSentence(mention);
        boolean contained = contextual.isContainedByName(mention);
        for (Candidate c : mention.getCandidates()) {
            c.setBeginningOfSentence(beginningOfSentence);
            c.setCapitalLetters(capitalLetters);
            c.setContained(contained);
            setSuspicion(c);
        }
    }

    private void setSuspicion(Candidate c) {
        String sf = c.getSurfaceForm();
        int suspicion = 0;

        if (!c.looksLikeAName()) {
            suspicion += 200;
        }
        if (c.isStopwords()) {
            suspicion += 200;
        }
        if (!c.isDefaultSense()) {
            suspicion += 50;
        }
        if (c.getCount() < 15) {
            suspicion += 50;
        }
        if (c.isContained()) {
            suspicion += 50;
        }

        suspicion -= sf.length()/5;
        c.setSuspicion(suspicion);
    }

    public Collection<Emit> tagText(HashMap<String, Mention> mentionsLookup) {
        //Tag text with newly generated keywords
        Trie.TrieBuilder builder = Trie.builder().onlyWholeWords();
        //.caseInsensitive()
        //.removeOverlaps()
        Iterator it = mentionsLookup.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            builder.addKeyword((String)pair.getKey());
            //System.out.println(pair.getKey());
        }
        Trie trie = builder.build();
        return trie.parseText(untagged);
    }


    public HashMap<String, Mention> createAcronymsAndParts() {
        HashMap<String, Mention> newMentions = new HashMap<>();
        for(Mention m : list) {
            String sf = m.getText(); //Asea Brown Boveri
            String acronym = ""; //ABB
            String dottedAcronym = ""; //A.B.B.
            String[] sfParts = sf.split(" "); // [Asea, Brown, Boveri]
            ArrayList<String> forms = new ArrayList<>(); //[Asea, Brown, Boveri, ABB, A.B.B., Asea Brown, Brown Boveri]
            if (sfParts.length>1) { //contains more than 1 word
                for(int i = 0; i < sfParts.length; i++) {
                    String part = sfParts[i];
                    if (part.length() == 0)
                        continue;

                    forms = addToForms(forms,part);
                    acronym += part.substring(0,1);
                    dottedAcronym += part.substring(0,1)+".";
                    if(i>0 && sfParts.length > 2) { //Combination of two words
                        forms = addToForms(forms,sfParts[i-1]+" "+sfParts[i]);
                    }
                    if(i>1 && sfParts.length > 3) { //Combination of three words
                        forms = addToForms(forms,sfParts[i-2]+" "+sfParts[i-1]+" "+sfParts[i]);
                    }
                }
                forms = addToForms(forms,acronym);
                forms = addToForms(forms,dottedAcronym);
                forms = addToForms(forms,dottedAcronym.substring(0,dottedAcronym.length()-1));
            }
//            String a = findAcronymInParenthesis(m);
//            if (a!=null) {
//                forms = addAcronymToForms(forms, a);
//            }
//            forms.add(sf.toUpperCase());
//            forms.add(sf.toLowerCase());
            for(String form: forms) {

                Mention mention = new Mention(0,0,form);
                for (Candidate candidate : m.getCandidates()) {
                    mention.addCandidate(new Candidate(form, candidate));
                }
                newMentions.put(form,mention);
            }
        }
        return newMentions;
    }

    private String findAcronymInParenthesis(Mention m) {
        String s = untagged.substring(m.getEnd()+1);
        if (s.length() > 1 && s.substring(0,1).equals("(")){
            int end = s.indexOf(")");
            if (end > 0)
                return s.substring(1,end);
        }
        return null;
    }

    private ArrayList<String> addToForms(ArrayList<String> forms, String form) {
        if(form.matches("(.*)\\p{Lu}(.*)")) {
            forms.add(form);
        }
        return forms;
    }

    private ArrayList<String> addAcronymToForms(ArrayList<String> forms, String form) {
        if(form.matches("(\\p{Lu}\\.?){2,}")) {
            forms.add(form);
        }
        return forms;
    }

}
