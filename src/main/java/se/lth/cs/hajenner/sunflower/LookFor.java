package se.lth.cs.hajenner.sunflower;

import se.lth.cs.hajenner.sunflower.model.Category;
import se.lth.cs.hajenner.sunflower.model.Resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by chabrol on 5/17/16.
 */
public class LookFor {

    private final Resources resources;

    public LookFor(Resources resources) {
        this.resources = resources;
    }

    public List<Category> findNBestCategories(int qNumber, int width){
        ArrayList<Category> categories = resources.get(qNumber);

        if (categories.size() > width) {
            return categories.subList(0, width);
        }
        return categories;
    }

    public List<Category> findCategoryOver(int qNumber, double ratiolimit){
        ArrayList<Category> categories= resources.get(qNumber);
        int num=0;

        while (categories.get(num).getRatio() > ratiolimit ){
            num++;
        }

        return categories.subList(0,num);

    }

    public List<Category> findCategoryDepth(int qNumber, int width, int depth){
        List<Category> categories = findNBestCategories(qNumber,width);

        List<Category> categoriesLastDepth = new ArrayList<>();
        for (Category category:categories) {
            categoriesLastDepth.add(category);
        }

        addNextDepth(categories, categoriesLastDepth, width, depth);

        return categories;
    }


    private void addNextDepth(List<Category> categories, List<Category> categoriesLastDepth, int width, int depth){
        ArrayList<Category> nextDepth = new ArrayList<>();

        if (depth ==1) {
            Collections.sort(categories);
            return;
        }

        for (Category category : categoriesLastDepth){
            List<Category> depth2 = findNBestCategories(category.getQNumber(),width);
            for (Category categoryDepth2 : depth2){
                int index = categories.indexOf(categoryDepth2);
                if (index > -1){
                    categories.get(index).setRatio(categories.get(index).getRatio()+categoryDepth2.getRatio()*category.getRatio());
                }
                else {
                    Category newCategory= new Category(categoryDepth2.getQNumber(),categoryDepth2.getRatio()*category.getRatio());
                    categories.add(newCategory);
                    nextDepth.add(newCategory);
                }
            }
        }
        addNextDepth(categories,nextDepth,width,depth-1);
    }

}
