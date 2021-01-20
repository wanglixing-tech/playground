package crs.fcl.integration.main;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class FileObserver implements Callable<Integer> {
    private final Integer timeInterval;
    private final String mailHandler;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");

    public FileObserver(String mailHandler, Integer timeInterval) {
        this.timeInterval = timeInterval;
        this.mailHandler = mailHandler;
   }
 
    @Override
    public Integer call() throws Exception {
    	int result = 1;

        try {
            if ((timeInterval == 0) || (timeInterval == 1)) {
            	return result;
            } else {       	
            	while (true) {
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());        		
                    System.out.println(sdf.format(timestamp) + "\twith " + mailHandler + "Execute observe program.");
                    TimeUnit.SECONDS.sleep(timeInterval);
                }
            }
        } catch (InterruptedException e) {
            throw new IllegalStateException("task interrupted", e);
        } 
    }
}