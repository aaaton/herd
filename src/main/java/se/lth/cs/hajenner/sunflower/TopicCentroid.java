package se.lth.cs.hajenner.sunflower;

import se.lth.cs.hajenner.sunflower.model.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by antonsodergren on 5/17/16.
 */
public class TopicCentroid {
    private List<Category> categories;
    private List<Category> normalizedCategories;
    private double squareSum = 0;
    private boolean hasBeenNormalized = false;

    public TopicCentroid() {
        categories = new ArrayList<>();
    }

    public void linearCombination(List<Category> newCategories) {
        for (Category c : newCategories) {
            int index = categories.indexOf(c);
            if (index >= 0) {
                categories.get(index).merge(c);
            } else {
                categories.add(c);
            }
        }
        hasBeenNormalized = false;
    }

    public double similarityTo(List<Category> other) {
        if(!hasBeenNormalized)
            normalize();

        double sum = 0,
        otherSquareSum = 0;

        for (Category c : other) {
            int index = normalizedCategories.indexOf(c);
            if (index >= 0) {
                sum += normalizedCategories.get(index).getRatio()*c.getRatio();
            }
            otherSquareSum += c.getRatio()*c.getRatio();
        }
        otherSquareSum = Math.sqrt(otherSquareSum);

        if(squareSum==0)
            calculateSquareSum();

        return sum/(squareSum*otherSquareSum);
    }

    private void calculateSquareSum() {
        double temp = 0;
        for (Category category : normalizedCategories) {
            temp += category.getRatio()*category.getRatio();
        }
        squareSum = Math.sqrt(temp);
    }

    private void normalize() {
        normalizedCategories = new ArrayList<>();
        normalizedCategories.addAll(categories);

        double max = findMax();
        for (Category c : normalizedCategories) {
            c.setRatio(c.getRatio()/max);
        }
        hasBeenNormalized = true;
    }

    private double findMax() {
        double max = 0;
        for (Category c : categories) {
            if (c.getRatio() > max)
                max = c.getRatio();
        }
        return max;
    }

}
