package crs.fcl.integration.main;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JavaReadZip {

    private final static Long MILLS_IN_DAY = 86400000L;

    public static void main(String[] args) throws IOException {

        String fileName = "src/resources/myfile.zip";

        try (FileInputStream fis = new FileInputStream(fileName);
                BufferedInputStream bis = new BufferedInputStream(fis);
                ZipInputStream zis = new ZipInputStream(bis)) {
            
            ZipEntry ze;

            while ((ze = zis.getNextEntry()) != null) {

                System.out.format("File: %s Size: %d Last Modified %s %n",
                        ze.getName(), ze.getSize(),
                        LocalDate.ofEpochDay(ze.getTime() / MILLS_IN_DAY));
            }
        }
    }
}