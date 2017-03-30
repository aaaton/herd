package se.lth.cs.hajenner.sunflower;


import se.lth.cs.hajenner.sunflower.model.Category;
import se.lth.cs.hajenner.sunflower.model.Resources;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Created by chabrol on 5/17/16.
 */
public class Main {

    public static void  main(String[] args){
            Instant start = Instant.now();
        Resources r = new Resources();
        LookFor lookFor = new LookFor(r);

            Instant end = Instant.now();
            long total = Duration.between(start, end).toMillis();
            System.out.println(total);

//            start = Instant.now();
        List<Category> obama=null, bush = null, sweden = null, cosine = null;
        for (int i = 0; i < 1000; i++) {
            obama = lookFor.findCategoryDepth(76, 3, 4);
            bush = lookFor.findCategoryDepth(207, 3, 4);
            sweden = lookFor.findCategoryDepth(34, 3, 4);
            cosine = lookFor.findCategoryDepth(1784941, 3, 4);
        }
//            end = Instant.now();
//            total = Duration.between(start, end).toMillis();
//            System.out.println(total);
        TopicCentroid tc = new TopicCentroid();

        tc.linearCombination(sweden);
        tc.linearCombination(bush);
        tc.linearCombination(obama);
//        tc.linearCombination(bush);
//        tc.linearCombination(sweden);
//        tc.linearCombination(sweden);
//        tc.linearCombination(sweden);
//        tc.linearCombination(cosine);
//        tc.linearCombination(bush);
//        tc.linearCombination(obama);

        System.out.println("bush: "+tc.similarityTo(bush));

        System.out.println("obama:" +tc.similarityTo(obama));

        System.out.println("sweden: "+tc.similarityTo(sweden));

        System.out.println("Cosine: "+tc.similarityTo(cosine));
//        printCategories(tc.categories);

//        categories=lookFor.findNBestCategories("Q34",5);
//        printCategories(categories);

//        categories=lookFor.findCategoryOver("Q34",0.1);
//        printCategories(categories);

//        List<Category> depth2=lookFor.findCategoryDepth("Q34",3,2);
//        printCategories(depth2);

    }

    private static void printCategories(List<Category> list) {
        list.forEach(System.out::println);
    }
}
