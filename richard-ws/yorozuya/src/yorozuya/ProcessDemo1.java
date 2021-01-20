package yorozuya;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class ProcessDemo1 {

   public static void main(String[] args) {

      try {
         ProcessBuilder pb = new
            ProcessBuilder("/bin/sh", "-c",
         "echo 'scale=24;22/7' | bc");
         final Process p=pb.start();
         BufferedReader br=new BufferedReader(
            new InputStreamReader(
               p.getInputStream()));
               String line;
               while((line=br.readLine())!=null){
                  System.out.println(line);
               }
      } catch (Exception ex) {
         System.out.println(ex);
      }
   }
}
