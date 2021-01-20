package crs.fcl.integration.main;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Stack;

public class TestStack {
    public static void main(String[] args) {
        Stack<String> stackOfPlates = new Stack<>();

        //stackOfPlates.add("Plate 1");
        //stackOfPlates.add("Plate 2");
        //stackOfPlates.add("Plate 3");
        //stackOfPlates.add("Plate 4");
        stackOfPlates.push("Plate 1");
        stackOfPlates.push("Plate 2");
        stackOfPlates.push("Plate 3");
        stackOfPlates.push("Plate 4");

//        
//        System.out.println("=== Iterate over a Stack using Java 8 forEach() method ===");
//        stackOfPlates.forEach(plate -> {
//            System.out.println(plate);
//        });

        
        while (!stackOfPlates.empty()) {
            String plate = stackOfPlates.pop();
            System.out.println(plate);
        }

    }
}