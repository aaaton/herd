package se.lth.cs.hajenner.evaluation;

import se.lth.cs.hajenner.HERD;
import se.lth.cs.hajenner.Mentions;
import se.lth.cs.hajenner.lucene.LuceneCommunicator;
import se.lth.cs.hajenner.Resources;
import se.lth.cs.hajenner.sunflower.Sunflower;
import se.lth.cs.hajenner.pagerank.PageRank;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Created by antonsodergren on 3/7/16.
 */
public class Evaluator {
    private static final String EVAL="src/main/resources/evaluation/";
    private static final String DETAILED_RESULTS="src/main/resources/evaluation/results-detailed/";
    public static final String ERD_GOLDEN_STANDARD=EVAL+"golden_standard_1906.tsv";
    public static final String ERD_DOCUMENT_PATH=EVAL+"documents/";
    public static final String AIDA_GOLD=EVAL+"AIDA/gold.tsv";
    public static final String AIDA_DOCUMENT_PATH=EVAL+"AIDA/documents/";
    public static final String AIDAYAGO_GOLD=EVAL+"AIDA-linked/gold.tsv";
    public static final String AIDAYAGO_DOCUMENTS=EVAL+"AIDA-linked/documents/";
    private static String language = "englishne";
    private static String core = "erd";
    private static String now;
    private static Sunflower sunflower;
    private static PageRank pr;
    private static LuceneCommunicator luceneCommunicator;
    public static int Count, Total;
    public static float Probability;

    public static void main(String[] args) {
        now = LocalDateTime.now().toString();
        sunflower = Resources.Instance.sunflower;
        pr = Resources.Instance.pageRank;
        luceneCommunicator = Resources.Instance.luceneCommunicator;
        full(language, "ERD-50", ERD_DOCUMENT_PATH, ERD_GOLDEN_STANDARD);
//        full(language, "AIDA/YAGO", AIDAYAGO_DOCUMENTS, AIDAYAGO_GOLD);
//        small(language, "AIDA", AIDA_DOCUMENT_PATH, AIDA_GOLD);
//        aida();
    }

    private static void small(String lang,String name, String docs, String gold) {
//        luceneCommunicator = new LuceneCommunicator(lang);
        ArrayList<String> results = new ArrayList<>();
        results.add("\n\n"+name+":\n");
        TSVParser tsvParser = new TSVParser(gold);
        File folder = new File(docs);
        File[] files = folder.listFiles();

        int matches = 0;
        int mentionsSize=0;
        int goldenSize=0;
        int length = 0;
        long total = 0;


        for(File file: files) {
            try {
                System.out.print(".");

                Instant start = Instant.now(); // measuring from here
                String text = new String(Files.readAllBytes(file.toPath()));
                HERD herd = new HERD(lang);
                Mentions mentions = herd.tag(text, luceneCommunicator,sunflower,pr);
                //to here
                Instant end = Instant.now();
                total += Duration.between(start, end).toMillis();
                length+=text.length();

                Evaluation evaluation = new Evaluation(mentions, tsvParser.getPart(file.getName()));

                results.add(file.getName()+": "
                        +"Precision: "+evaluation.getPrecision()+", "
                        +"Recall: "+evaluation.getRecall()+", "
                        +"F-Score: "+evaluation.getFScore()+", ");

                matches+= evaluation.getMatch();
                mentionsSize += evaluation.getMentionsSize();
                goldenSize += evaluation.getGoldenSize();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println();


        float precision = ((float)matches)/mentionsSize;
        float recall = ((float)matches)/goldenSize;
        float fScore = 2*precision*recall/(precision+recall);
        results.add("\n\n");
        float divider = ((float)length)/5000f;
        results.add(total/divider+"ms per 5000 characters");
        results.add(name+" "+now);
        results.add("Micro average:");
        results.add("\tPrecision: "+precision);
        results.add("\tRecall: "+recall);
        results.add("\tF-Score: "+fScore);

        results.forEach(System.out::println);
        log(results);

    }

    public static void full(String lang, String name,String docpath, String golden) {
//        luceneCommunicator = new LuceneCommunicator(lang);
        ArrayList<String> results = new ArrayList<>();
        results.add("\n\n"+name);

        TSVParser tsvParser = new TSVParser(golden);
        File folder = new File(docpath);
        File[] files = folder.listFiles();

        int matches = 0;
        int disambiguated = 0;
        int mentionsSize=0;
        int goldenSize=0;
        int dgSize = 0;
        int dmSize = 0;
        int length=0;
        long total = 0;
        int count = 0;
        int max = 1000;
        for(File file: files) {
            try {
                count++;
                if (count > max) {
                    break;
                }
                System.out.print(".");


                // measuring from here
                Instant start = Instant.now();

                String text = new String(Files.readAllBytes(file.toPath()));
                HERD herd = new HERD(lang);
                Mentions mentions = herd.tag(text, luceneCommunicator,sunflower, pr);

                //to here
                Instant end = Instant.now();
                total += Duration.between(start, end).toMillis();
                length+=text.length();

                Evaluation evaluation = new Evaluation(mentions, tsvParser.getPart(file.getName()));

                results.add(file.getName()+": "+
                    "Precision: "+evaluation.getPrecision()+", "+
                    "Recall: "+evaluation.getRecall()+", "+
                    "F-Score: "+evaluation.getFScore()+", "+
                    "Precision D: "+evaluation.getPrecisionDisambiguated()+", "+
                    "Recall D: "+evaluation.getRecallDisambiguated()+", "+
                    "Disambiguated: "+evaluation.getFScoreDisambiguated());
                matches+= evaluation.getMatch();
                disambiguated += evaluation.getDisambiguated();
                mentionsSize += evaluation.getMentionsSize();
                goldenSize += evaluation.getGoldenSize();
                dmSize += evaluation.getDisambiguatedMentionsSize();
                dgSize += evaluation.getDisambiguatedGoldenSize();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
        System.out.println(total/files.length+"ms per file");

        float precision = ((float)matches)/mentionsSize;
        float precisionD = ((float)disambiguated)/dmSize;
        float recall = ((float)matches)/goldenSize;
        float recallD = ((float)disambiguated)/dgSize;
        float fScore = 2*precision*recall/(precision+recall);
        float fScoreD = 2*precisionD*recallD/(precisionD+recallD);

        results.add("\n\n");
        float divider = ((float)length)/5000f;
        results.add(total/divider+"ms per 5000 characters");
        results.add("Recognition: "+name+" "+now);
        results.add("Micro average: ");
        results.add("\tPrecision: "+precision);
        results.add("\tRecall: "+recall);
        results.add("\tF-Score: "+fScore);
        results.add("");

        results.add("Disambiguation: "+name+" "+now);
        results.add("Micro average: ");
        results.add("\tPrecision: "+precisionD);
        results.add("\tRecall: "+recallD);
        results.add("\tF-Score: "+fScoreD);

        results.forEach(System.out::println);
        log(results);
    }

    private static void log(ArrayList<String> results) {
        Path file = Paths.get(EVAL+"results-log.txt");
        Path detailed = Paths.get(DETAILED_RESULTS+now+".txt");
        ArrayList<String> overview = new ArrayList<>();
        for (int i = results.size()-13; i < results.size(); i++) {
            overview.add(results.get(i));
        }
        try {
            Files.write(detailed,results,Charset.forName("UTF-8"));
            Files.write(file, overview, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
        } catch (NoSuchFileException e) {
            createFile(file, overview);
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createFile(Path file, ArrayList<String> results) {
        try {
            Files.write(file, results, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


