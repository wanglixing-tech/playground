package crs.fcl.integration.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
/**
 * <pre>
 * Basic requirements
 * 1.	Run as a command line so package it as a jar and provide a cmd file to run it
 * 2.	Add a property file to configure the paths and a cache file to store the last run date
 * 3.	The output will be on a ToGK directory and you can use the file name convention as BestImage_YYYYMMDDHHMISS
 * 4.	Add a filter to look for only jpeg files
 * 
 * @author Navin Khanna <Navin.Khanna@fcl.crs>
 * 		   Richard.Wang <Richard.Wang@fcl.crs>
 * Created on 2019/10/01 
 * Modified on 2019/10/07 
 * Modified on 2019/11/12  Modified to process GS1 images from mandatory to at will
 * Modified on 2020/01/27  Big changes:
 *   1) Both *_P.jpg and *.p.jpg are valid
 *   2) Instead of using file comparator, directly reflect the rule given in blue print: as priority 711>1>P/p
 *   3) Due to the file last modified timestamp is not able to reflect the file's updated timestamp in case
 *      the files are being copied, changed to use image file creation time stamp instead of the last modified 
 *      timestamp
 * </pre>
 */
public class BestImage {
	/*
	 * Fundamental fixed files' names, which must be ready in the same folder where 
	 * this utility program located.
	 */
	private static final String bestImagesPropertiesFileName = "best_images.properties";
	private static final String bestImagesTimeCacheFileName = "timecache";
	/*
	 * Followings are from properties file
	 */
	private static int regularFileNameLength = 11;
	private static String srcImageFileType = ".jpg";
	private static String destImageFileType = ".jpg";
	private static Path path2GS1Images = null;
	private static Path path2FCLImages = null;
	private static Path path2BestImageList = null;
	private static long lastRunTime = 0;
	public static void main(String[] args) throws IOException, ParseException {
		String currentDir = System.getProperty("user.dir");
		File propFile = new File(currentDir + File.separator + bestImagesPropertiesFileName);
		if (propFile.exists()) {
			if (!uploadProperties(currentDir + File.separator + bestImagesPropertiesFileName)) {
				System.out.println("Above error has been detected while uploading properties file, please fix the problem and try again.");
				System.exit(999);				
			};
		} else {
			System.out.println("Could not find 'best_images.properties' file in current folder, which is mandatory.");
			System.exit(999);
		}
		
		File timeCacheFile = new File(currentDir + File.separator + bestImagesTimeCacheFileName);
		if (timeCacheFile.exists()) {
			 lastRunTime = getLastRunTimeFromFile(currentDir + File.separator + bestImagesTimeCacheFileName).getTime();
		} else {
			System.out.println("Could not find 'timecache' file in current folder, all best image files will become the target of this utility, which could be a great number...... ");		
		}
		
		if(! path2FCLImages.toFile().exists()) {
			System.out.println("Not any valid image folders specified in the properties file, please provide at least one.");
			System.exit(999);
		}

		if(! path2BestImageList.toFile().exists()) {
			System.out.println("Specified path for keeping best image list file does not exist, create it.");
			path2BestImageList.toFile().mkdir();
		}

		
		/**
		 * GS1Images folder keeps all images transfered from GS1 via Web service, we only focus on 
		 * those files their names are ended with '_711' and '_1';
		 * 
		 * FCLImages folder keeps image files created/uploaded by operators and their file names 
		 * are ended with '_P' and '_B', but we only focus on the images with '_P'.
		 * 
		 */
		/*
		 * merge GS1 and FCL images here
		 */
		Set<Path> mergedTargetImageFiles = new HashSet<Path>();

		/*
		 * Collect GS1 images
		 */
		List<Path> targetImagePaths4GS1 = null;
		if (path2GS1Images != null) {
			targetImagePaths4GS1 = Files.list(path2GS1Images)
					.filter(s -> s.toString().endsWith("_711" + srcImageFileType)
								|| s.toString().endsWith("_1" + srcImageFileType))
					.collect(Collectors.toList());
			System.out.println("Total " + targetImagePaths4GS1.size()  + " candidates have been selected from " + path2GS1Images);			
			mergedTargetImageFiles.addAll(targetImagePaths4GS1);
		}
		
		/*
		 * Collect FCL images
		 */
		List<Path> targetImagePaths4FCL = Files.list(path2FCLImages)
				.filter(s -> s.toString().endsWith("_P" + srcImageFileType) || s.toString().endsWith("_p" + srcImageFileType))
				.collect(Collectors.toList());
		System.out.println("Total " + targetImagePaths4FCL.size()  + " candidates have been selected from " + path2FCLImages);
		mergedTargetImageFiles.addAll(targetImagePaths4FCL);
		
        ArrayList<Path> targetFileList = new ArrayList<Path>();
        
        Set<String> changedImageUPC = getUPCSinceLastRun(mergedTargetImageFiles, lastRunTime);
        if (changedImageUPC.isEmpty()) {
            System.out.println("Nothing changed for all best images.");
        	
        } else {
        	System.out.println("Total " + changedImageUPC.size() + " UPC's best images have been changed.");
	        for (String upc: changedImageUPC) {
	        	boolean exist = false;
	        	// Check upc_711.jpg
	            for (Path file : mergedTargetImageFiles) {
	            	String srcFileName = file.toFile().getName().toLowerCase();
	            	String highFileName = upc + "_711" + srcImageFileType;
	                if (srcFileName.equals(highFileName)) {
	                    targetFileList.add(file);
	                    exist = true;
	                    break;
	                }
	            }
	            
	        	// Check upc_1.jpg
	            if (! exist) {
		            for (Path file : mergedTargetImageFiles) {
		            	String srcFileName = file.toFile().getName().toLowerCase();
		            	String middleFileName = upc + "_1" + srcImageFileType;
		                if (srcFileName.equals(middleFileName)) {
		                    targetFileList.add(file);
		                    exist = true;
		                    break;
		                }
		            }
	            }
	            
	        	// Check upc_P.jpg
	            if (! exist) {
		            for (Path file : mergedTargetImageFiles) {
		            	String srcFileName = file.toFile().getName().toLowerCase();
		            	String bottomFileName = upc + "_p" + srcImageFileType;
		                if (srcFileName.equals(bottomFileName)) {
		                    targetFileList.add(file);
		                    exist = true;
		                    break;
		                }
		            }
	            }
	        }
        }

        /*
         * toGK file name: BestImage_YYYYMMDDHHMISS
         */
        if (! targetFileList.isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Date curDate = new Date();
            String fileNameToGK = path2BestImageList + File.separator + "BestImage_" + sdf.format(curDate);
            System.out.println("Output best image list to GK file:" + fileNameToGK);
            FileWriter fileWriter = new FileWriter(fileNameToGK, true);
            PrintWriter writer = new PrintWriter(fileWriter);
        	for (Path tfn : targetFileList) {
	        	String srcFileName = tfn.getParent().toFile().getName() + File.separator + tfn.toFile().getName();
	        	String descFileName = shapedDestFileName(tfn);
	            writer.println(srcFileName + " " + descFileName);     		
        	}
	        writer.close();   
        } else {
        	System.out.println("No new best image list created.");
        }

    	System.out.println("Updating timeCache file......");
        saveCurrentTimeStampToFile(currentDir + File.separator + "timeCache");
    	System.out.println("Done.");
    }

    private static Set<String> getUPCSinceLastRun(Set<Path> candidateImages, long lastRuntime) {
        Set<String> returnSet = new HashSet<String>();
        for (Path path2file : candidateImages) {
       	    File imageFile = path2file.toFile();
       	    long creationTime = 0;
       	    try {
       	        FileTime cTime = (FileTime) Files.getAttribute(Paths.get(imageFile.toURI()), "creationTime");
       	        creationTime = cTime.toMillis();
       	    } catch (IOException ex) {
       	    }
       	    
       	    if (creationTime > lastRuntime) {
      		   returnSet.add(getUPC(imageFile.getName()));
       	    }
        }
        return returnSet;
    }

	static String getUPC(String imageFileName) {
		int underscorePosition = imageFileName.indexOf("_");
		return imageFileName.substring(0, underscorePosition);
	}

    static String getShotType(String imageFileName) {
        int underscorePosition =imageFileName.indexOf("_");
        int dotPosition = imageFileName.indexOf(".");
        return imageFileName.substring(underscorePosition+1, dotPosition);
    }
    
	private static void saveCurrentTimeStampToFile(String timeCacheFile) throws IOException {
		String currentTimeStamp = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date());
		FileWriter fileWriter = new FileWriter(timeCacheFile);
		PrintWriter printWriter = new PrintWriter(fileWriter);
		printWriter.print("LAST_RUN_TIME=" + currentTimeStamp);
		printWriter.close();
	}

	private static Date getLastRunTimeFromFile(String timeCacheFile)
			throws FileNotFoundException, IOException, ParseException {
		Properties timeProps = new Properties();
		timeProps.load(new FileInputStream(timeCacheFile));
		String strLastRunTime = timeProps.getProperty("LAST_RUN_TIME");
		System.out.println(strLastRunTime);
		Date lastRunTime = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").parse(strLastRunTime);
		return lastRunTime;
	}

	private static boolean uploadProperties(String bestImagesPropFile)
			throws FileNotFoundException, IOException, ParseException {
		Properties bestImagesProps = new Properties();
		bestImagesProps.load(new FileInputStream(bestImagesPropFile));

		String ift = bestImagesProps.getProperty("FILE_TYPE");
		if (isNullOrEmpty(ift)) {
			System.out.println("'FILE_TYPE' property has not been defined in the properties file, take '.jpg' as the default.");
		} else {
			srcImageFileType = ift.toLowerCase();	
			destImageFileType = srcImageFileType; 
		}
		
		String p2GS1Images = bestImagesProps.getProperty("PATH_TO_GS1_IMAGES");
		
		if (isNullOrEmpty(p2GS1Images)) {
			System.out.println("'PATH_TO_GS1_IMAGES'has not been defined in the properties file, ignore GS1 images.");
			path2GS1Images = null;
		} else {
			path2GS1Images = Paths.get(p2GS1Images);
		}
		
		String p2FCLImages = bestImagesProps.getProperty("PATH_TO_FCL_IMAGES");
		
		if (isNullOrEmpty(p2FCLImages)) {
			System.out.println("'PATH_TO_FCL_IMAGES' has not been defined in the properties file.");
			return false;
		} else {
			path2FCLImages = Paths.get(p2FCLImages);
		}
		
		String p2bil = bestImagesProps.getProperty("PATH_TO_BEST_IMAGE_LIST");
		if (isNullOrEmpty(p2bil)) {
			System.out.println(" 'PATH_TO_BEST_IMAGE_LIST' property has not been defined in the properties file, it's mandatory.");
			return false;
		} else {
			path2BestImageList = Paths.get(p2bil);
		}
		
		return true;

	}
	
	public static String shapedDestFileName(Path srcFilePath) {
	String descFileName = srcFilePath.toFile().getName();
   	descFileName = descFileName.replaceAll("\\_.+$", "");
		StringBuilder builder = new StringBuilder(descFileName);
		while (builder.length() > regularFileNameLength) {
			if (builder.charAt(0) == '0')
				builder.deleteCharAt(0);	
			else 
				break;
		}
		
		while (builder.length() < regularFileNameLength) {
			builder.insert(0, '0');
		}
		
		return builder.toString() + destImageFileType;
	}
	
    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
    }
}
