package se.lth.cs.hajenner.sunflower.reader;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import se.lth.cs.hajenner.sunflower.model.Category;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by chabrol on 5/17/16.
 */
public class ReadMap {
    private String dataPathArticle;
    private String dataPathCategory;
    private Int2ObjectOpenHashMap<ArrayList<Category>> smallMap;

    public ReadMap(String dataPathArticle, String dataPathCategory) {
        this.dataPathArticle = dataPathArticle;
        this.dataPathCategory = dataPathCategory;
        smallMap = new Int2ObjectOpenHashMap<>();
    }

    public Int2ObjectOpenHashMap<ArrayList<Category>> getMap(){
        readFile(dataPathArticle);
        readFile(dataPathCategory);
        return smallMap;
    }

    private void readFile(String path) {
        try {

            BufferedReader buff = new BufferedReader(new InputStreamReader(getClass()
                    .getClassLoader()
                    .getResourceAsStream(path)));

            String line;
            int qArticle;
            int numLanguages;

            while (((line = buff.readLine()) != null)) {


                String[] line_i = line.split("\t");
                int nb_cat = (line_i.length-2)/2;

                qArticle = qStringToInt(line_i[0]);
                numLanguages = Integer.parseInt(line_i[1]);
                ArrayList<Category> categories= new ArrayList<>();

                for (int i=0; i < nb_cat ; i++){

                    Category category= new Category(qStringToInt(line_i[2+2*i]),Integer.parseInt(line_i[2+2*i+1]),numLanguages);
                    categories.add(category);
                }
                Collections.sort(categories);

                smallMap.put(qArticle,categories);

            }

            buff.close();
        }
        catch (IOException e) {
            System.out.print(e.getMessage());
        }

    }

    private int qStringToInt(String qNumber) {
        return Integer.parseInt(qNumber.substring(1));
    }
}