package se.lth.cs.hajenner.evaluation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by antonsodergren on 3/7/16.
 */
public class TSVParser {
    private Path path;
    private HashMap<String, List<GoldenMention>> goldStandard;

    public TSVParser(String filePath) {
        this.path = Paths.get(filePath);
        goldStandard = new HashMap<>();
        try {
            List<String> lines = Files.readAllLines(path);
            for(String line : lines) {
                //0=name, 1=startbyte, 2=endbyte, 3=freebase, 4=freebase, 5=mention, 6&7=nonsense
                String[] words = line.split("\t");
                if (words.length < 3) {
                    continue;
                }

                int start = Integer.parseInt(words[1]);
                int end = Integer.parseInt(words[2]);
                GoldenMention mention = new GoldenMention(start,end,words[5],words[4]);

                List<GoldenMention> mentions;
                if(goldStandard.containsKey(words[0])) {
                    mentions = goldStandard.get(words[0]);
                } else {
                    mentions = new ArrayList<>();
                }
                mentions.add(mention);
                goldStandard.put(words[0],mentions);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<GoldenMention> getPart(String filename) {
        filename = cleanUpName(filename);
        return goldStandard.get(filename);
    }


    private String cleanUpName(String filename) {
        return filename.substring(0,filename.length()-4);
    }
}
