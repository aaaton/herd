package se.lth.cs.hajenner.sunflower.model;

import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import se.lth.cs.hajenner.sunflower.reader.ReadMap;
import java.util.ArrayList;

/**
 * Created by chabrol on 5/17/16.
 */
public class Resources {

    private Int2ObjectOpenHashMap<ArrayList<Category>> generalMap;
    public static String path = "dataset/";
    private String general = path+"GeneralMap.txt";
    private String category = path+"CategoryMap.txt";

    public Resources() {
        initiateResources();
    }

    private void initiateResources() {
        System.out.println("Reading Sunflower data.");
        generalMap = new ReadMap(general,category).getMap();
    }


    public ArrayList<Category> get(int qNumber) {
        if(generalMap.containsKey(qNumber)) {
            ArrayList<Category> categories = new ArrayList<>();
            for (Category c : generalMap.get(qNumber)) {
                categories.add(new Category(c));
            }
            return categories;
        }
        return new ArrayList<>();
    }
}
