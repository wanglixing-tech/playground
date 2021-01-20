User-based number of processes and number of open files check program
1.	Instead of collecting all related information from all users, this script defines a user list you are most interested in, for example “iib” and “mqm”;
2.	The information is focusing on number of processes the user is running and number of files those processes opened;
3.	Regarding the report file format, used a simple and easy to understand and easy to be processed format; 
4.	Keeping the collected information saved in one report file instead of create new file for every time running the script. The benefit is that we can know what user at what time has how many processes and opened files at one sight, and easily manipulate the file with excel, for example to create any charts to see the trends for the past time period.
5.	Using “logrotate” utility to manage the files.

From Alex:
•	Pull the user list out of the script; create a properties file in the same folder for the time being.
•	Add the maximum number of processes and maximum number of open files.
    NPmax=`su $i -c 'ulimit -u'`
    NOmax=`su $i -c 'ulimit -n'` 
But, if the userId who invokes this script is not a superuser, above commands will ask password.
Because /etc/security/limits.conf has read-only permission for all users, alternatively, let’s directly pick up the items based on the username.
If not found in the limits.conf file, type “N/A”
•	Limited the user list to :
In the file, allow comment lines appears, skip them
    dbadmin
    db2inst1
    # abcd
    esbdbid
 
•	The report log file is named report.log for now, and remove the $$ (PID) from the name in order for using the unique one.
•	Create logrotate.conf file to manage report.log rotation and generations
