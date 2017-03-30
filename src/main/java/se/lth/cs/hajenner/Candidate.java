package se.lth.cs.hajenner;

//import org.json.JSONArray;
//import org.json.JSONObject;
import se.lth.cs.hajenner.evaluation.Evaluator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by antonsodergren on 2/19/16.
 * Candidate is a SurfaceForm that has matched something in the text, and attributes concerning this surface form
 */
public class Candidate {
    private int solrID;
    private String jsonString;
    private String qID;
    private String surfaceForm;
    private int count;
    private int total;
    private int suspicion;
    private boolean generic;
    private boolean stopwords;
    private boolean onlywords;
    private boolean common;
    private boolean lowercase;

    //Contextual data
    private boolean isContained;
    private boolean isInHeadline;
    private boolean beginningOfSentence;
    private int capitalLetters;

    private int qNumber;
    private float invertedSuspicion;
    private double pageRankFactor=1d;
    private double probability = 1.0;
    private double similarity = 1.0;


    public Candidate(Candidate c) {
        jsonString = c.jsonString;
        surfaceForm = c.surfaceForm;
        qID = c.qID;
        total = c.total;
        count = c.count;
        generic = c.generic;
        stopwords = c.stopwords;
        onlywords = c.onlywords;
        common = c.common;
        lowercase = c.lowercase;
        suspicion = c.suspicion;
        invertedSuspicion = c.invertedSuspicion;
        probability = c.probability;
        similarity = c.similarity;
    }

    public Candidate(String surfaceForm, String QID, int total, int count) {
        this.surfaceForm = surfaceForm;
        this.qID = QID;
        this.total = total;
        this.count = count;
    }
    public Candidate(String form, Candidate c) {
        this(c);
        this.surfaceForm = form;
    }

    public Candidate(int solrID, String surfaceForm, String QID, int total, int count, double probability, boolean stopwords, boolean onlywords, boolean generic, boolean common, boolean lowercase) {
        this.solrID = solrID;
        this.surfaceForm = surfaceForm;
        this.qID = QID;
        this.total = total;
        this.count = count;
        this.probability = probability;
        this.stopwords = stopwords;
        this.onlywords = onlywords;
        this.generic = generic;
        this.common = common;
        this.lowercase = lowercase;
    }


    public String getSignature() {
        return qID +surfaceForm;
    }
    public int getCount() {
        return count;
    }

    public void setCount(int c) {
        count = c;
    }

    public int getTotal() {
        return total;
    }

    public String getJsonString() {
        return jsonString;
    }

    public int getSolrID() { return solrID; }

    public String getQID() {
        return qID;
    }

    public int getQNumber() {
        if (qNumber == 0) {
            qNumber = Integer.parseInt(qID.substring(1));
        }
        return qNumber;
    }

    public String getSurfaceForm() {
        return surfaceForm;
    }

    public boolean isOnlywords() {
        return onlywords;
    }

    public boolean isStopwords() {
        return stopwords;
    }

    public boolean isGeneric() {
        return generic;
    }

    public boolean isCommon() {
        return common;
    }

    public boolean isOneWord() {
        return surfaceForm.split(" ").length == 1;
    }


    public boolean isDefaultSense() {
        return (float)count/total > 0.5;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Candidate) {
            Candidate t = (Candidate) o;
            return t.solrID == solrID;
        }
        return false;
    }

    public boolean exactEquals(Object o) {
        return super.equals(o);
    }

    @Override
    public String toString() {
        return surfaceForm+"|"+ qID;
    }

    public String getLink() {
        return "<a href=\"http://wikidata.org/wiki/"+ qID +"\" target=\"_blank\">"+ qID +"</a>";
    }

    public int getSuspicion() {
        return (int) (suspicion/pageRankFactor);
    }

    public void setSuspicion(int suspicion) {
        this.suspicion = suspicion;
        if (suspicion<0)
            suspicion=0;
        invertedSuspicion = 100f / (100f+suspicion);
    }
    public float getInvertedSuspicion() {
        return invertedSuspicion;
    }

    public void setPageRankFactor(double pageRankFactor) {
        this.pageRankFactor = pageRankFactor;
    }

    public double getProbability() {
        return probability;
    }

    public double getWeight() {
        return count*similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public boolean isContained() {
        return isContained;
    }

    public void setContained(boolean contained) {
        isContained = contained;
    }

    public boolean isInHeadline() {
        return isInHeadline;
    }

    public void setInHeadline(boolean inHeadline) {
        isInHeadline = inHeadline;
    }

    public void setBeginningOfSentence(boolean beginningOfSentence) {
        this.beginningOfSentence = beginningOfSentence;
    }

    public void setCapitalLetters(int capitalLetters) {
        this.capitalLetters = capitalLetters;
    }

    public boolean looksLikeAName() {
        if(capitalLetters==0)
            return false;
        if (capitalLetters==1 && beginningOfSentence)
            return false;
        if (capitalLetters==1 && onlywords)
            return false;
        return true;
    }

    public boolean looksLikeAGenericName() {
        if (capitalLetters == 0)
            return false;
        if (capitalLetters == 1 && beginningOfSentence && onlywords)
            return false;
        return true;
    }
}
