package crs.fcl.integration.iib;
import java.util.LinkedList;

import java.util.LinkedList;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;
import name.fraser.neil.plaintext.diff_match_patch.Operation;
 
/**
 * Using google-diff-match-patch java library to get the differences between 2 strings.
 * @author Xuan Ngo
 */
public class StringDifference {
 
  public static void main(String[] args)
  {
    /**
     * diff_main(text1, text2) => diffs:
     *    An array of differences is computed which describe the transformation of text1 into text2. 
     *    Each difference is an array (JavaScript, Lua) or tuple (Python) or Diff object (C++, C#, Objective C, Java). 
     *    The first element specifies if it is an insertion (1), a deletion (-1) or an equality (0). 
     *    The second element specifies the affected text.
     *  
     *    diff_main("Good dog", "Bad dog") => [(-1, "Goo"), (1, "Ba"), (0, "d dog")]
     *  
     *    For more info, see https://github.com/google/diff-match-patch/wiki/API
     */
    diff_match_patch difference = new diff_match_patch();
    LinkedList<Diff> deltas = difference.diff_main("Good dog", "Good dog");
 
    // Reconstruct texts from the deltas
    //  text1 = all deletion (-1) and equality (0).
    //  text2 = all insertion (1) and equality (0).
    String text1 = "";
    String text2 = "";
    for(Diff d: deltas)
    {
      if(d.operation==Operation.DELETE)
        text1 += d.text;
      else if(d.operation==Operation.INSERT)
        text2 += d.text;
      else
      {
        text1 += d.text;
        text2 += d.text;
      }
    }
 
    System.out.println(text1);
    System.out.println(text2);    
 
  }
 
}
