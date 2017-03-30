package se.lth.cs.hajenner.sunflower.model;

import java.io.Serializable;

/**
 * Created by chabrol on 5/17/16.
 */
public class Category implements Comparable, Serializable{
    /*
    * @serial
     */
    private int qNumber;
    /*
    * @serial
     */
    private double ratio;

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public double getRatio() {
        return ratio;
    }
    public int getQNumber(){
        return qNumber;
    }

    public Category(int qNumberC,int numLanguagesC, int numLanguagesA){
        this.qNumber = qNumberC;
        this.ratio = ((double)numLanguagesC / numLanguagesA);
    }
    public Category(Category c) {
        this.qNumber = c.qNumber;
        this.ratio = c.ratio;
    }

    public Category(int qNumberC,double ratio){
        this.qNumber=qNumberC;
        this.ratio= ratio;
    }

    public double merge(Category category) {
        ratio+= category.ratio;
        return ratio;
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof Category) {
            Category cat = (Category) o;
            if (ratio < cat.getRatio()) {
                return 1;
            } else if (ratio == cat.getRatio()) {
                return 0;
            } else return -1;
        }
        return -1;
    }

    @Override
    public String toString() {
        return "\thttp://www.wikidata.org/wiki/"+qNumber+" :"+ratio;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Category) {
            return qNumber == ((Category)o).qNumber;
        }
        return false;
    }


}
