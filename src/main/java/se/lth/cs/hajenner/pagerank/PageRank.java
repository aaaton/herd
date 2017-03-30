package se.lth.cs.hajenner.pagerank;

import it.unimi.dsi.fastutil.ints.*;
import se.lth.cs.hajenner.Candidate;
import se.lth.cs.hajenner.Resources;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by antonsodergren on 4/26/16.
 */
public class PageRank {
    private Int2ObjectOpenHashMap<IntArrayList> map;
    private final String RANK_FILE = "/mnt/2_TB_HD/dataset/wikipedia/parsed/en/isLinked/restructured.tsv";

    public PageRank(String file) {
//        serialize();
        deserialize(file);
    }


    private void deserialize(String path) {
        System.out.println("Reading PageRank data.");
        try {
//            InputStream fileIn = getClass().getClassLoader().getResourceAsStream(SERIALIZED);
            FileInputStream fileIn = new FileInputStream(path);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            map = (Int2ObjectOpenHashMap<IntArrayList>) in.readObject();
            in.close();
            fileIn.close();
        } catch(IOException i) {
            i.printStackTrace();
            return;
        } catch(ClassNotFoundException c) {
            System.out.println("Int2Object not found");
            c.printStackTrace();
            return;
        }
    }


    private void serialize() {
        readMap();
        System.out.println("Serializing data");
        try
        {
            FileOutputStream fileOut = new FileOutputStream(Resources.pagerankPath);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(map);
            out.close();
            fileOut.close();
            System.out.println("Serialized data is saved in "+ Resources.pagerankPath);
        }catch(IOException i)
        {
            i.printStackTrace();
        }
    }

    private void readMap() {
        System.out.println("reading text file");
        List<String> lines = readFileToLines();
        System.out.println("creating map");
        map = new Int2ObjectOpenHashMap();
        int count = 0;
        for (String line : lines) {
            String[] numbers = line.split(" ");
            if(numbers.length > 1) {
                int key = Integer.parseInt(numbers[0]);
                IntArrayList list = new IntArrayList();
                for (int i = 1; i < numbers.length; i++) {
                    int val = Integer.parseInt(numbers[i]);
                    list.add(val);
                }
                map.put(key,list);
            }

            if (count == lines.size()/100) {
                System.out.print(".");
                count = 0;
            }
            count++;
        }
        System.out.println();
    }

    private List<String> readFileToLines() {
        try {
            File path = new File(RANK_FILE);
            List<String> lines = Files.readAllLines(path.toPath());
            return lines;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void CalculateRanks(ArrayList<Candidate> candidates) {
        Int2ObjectOpenHashMap<ArrayList<Page>> candidateMap = setupLinks(candidates);
        for (int i = 0; i < 3; i++) {
            step(candidateMap);
        }

        double sum = sum(candidateMap);
        for (Candidate c: candidates) {
            for (Page page : candidateMap.get(getIntID(c.getQID()))) {
                if(page.is(c)) {
                    c.setPageRankFactor(page.getScoreChange());
                }
            }
        }
    }

    private double sum(Int2ObjectOpenHashMap<ArrayList<Page>> candidateMap) {
        IntSet keys = candidateMap.keySet();
        double sum = 0f;
        for (Integer key : keys) {
            for (Page page : candidateMap.get(key)) {
                sum += page.getScore();
            }
        }
        return sum;
    }

    private void step(Int2ObjectOpenHashMap<ArrayList<Page>> candidates) {
        IntSet set = candidates.keySet();
        for (Integer key : set) {
            for (Page page : candidates.get(key)) {
                page.updateScore(candidates);
            }
        }
        //TODO: do this in a smarter way...
        for (Integer key : set) {
            candidates.get(key).forEach(Page::iterationDone);
        }
    }

    private Int2ObjectOpenHashMap<ArrayList<Page>> setupLinks(ArrayList<Candidate> candidates) {
        Int2ObjectOpenHashMap<ArrayList<Page>> candidateMap = new Int2ObjectOpenHashMap<>();
        double initVal = 1f/candidates.size();

        for (Candidate candidate : candidates) {
            int key = getIntID(candidate.getQID());
            Page p = new Page(candidate, initVal); //candidate.getInvertedSuspicion()
            if (!candidateMap.containsKey(key)) {
                candidateMap.put(key,new ArrayList<>());
            }
            candidateMap.get(key).add(p);
        }
        IntSet set = candidateMap.keySet();
        for (Integer key : set) {
            if (map.containsKey(key)) {
                IntArrayList list = map.get(key);
                for (Integer link : list) {
                    if (candidateMap.containsKey(link)) {
                        for (Page page : candidateMap.get(key)) {
//                            if (!link.equals(key))
                                page.addLink(link);
                        }
                    }
                }
            }
            for (Page page : candidateMap.get(key)) {
                page.setDivisor(candidateMap);
            }
        }
        return candidateMap;
    }

    private int getIntID(String id) {
        return Integer.parseInt(id.substring(1));
    }
}
