Runnable jar file, which contains all dependencies.
Usage:
java -jar poc-aws-s3-1.0.jar aws-bucket-name aws-object-key


Example:
java -jar java -jar poc-aws-s3-1.0.jar fcl-export staging/FCL-export-2020-03-03.xml


Note-1: aws-object-key: is used for "startAfter" 
 
Note-2: for the time being, all downloaded objects will be saved into folder:
C:\Temp\FCL_OBJS_FROM_AWS

