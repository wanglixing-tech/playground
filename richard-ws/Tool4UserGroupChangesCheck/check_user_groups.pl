#!/usr/bin/perl -w
##########################################################################################
#### This utility is to check the groups for one or more users
#### Case-one: 
#### 	The specific userId can be passed from command_line, then this utility will 
#### 	show if it is a member of any targeted groups;
####
#### Case-two:
####    If no userId is specified from command_line, as default, the utility will 
####    list up all users' membership of targeted groups, respectively. 
####   
#### This is the V2 of user_group_check.sh, rewriten by Perl
#### By: Richard Wang
#### On: 08/18/2018
#########################################################################################
use strict;
use warnings;
use Term::ANSIColor qw( colored );
use Cwd qw(getcwd);
#########################################################################################
#### The target group list must be defined here, which will be the base of creating a
#### bk_master.cf file as a standard reference
#########################################################################################
my $backup_master = "bk_master.cf";
my @targetGroups = ( "apmsvc", "mquser", "mqsupp", "mqadmin", "mqbrkrs", "ibuser", "ibsupp", "ibadmin" );
my @userId = ("UserId");
my %row4p;
my $report;

my ( $loginId, $email ) = @ARGV;
my @groupNames = ();
if (not defined $loginId ) {
 	$loginId = $ENV{LOGNAME} || $ENV{USER} || getpwuid($<);
	print ("No username is provided," .  colored($loginId, 'blue') . " Utility will check the groups it belongs to......\n");
}else {
	print ("You passed username:" . colored($loginId, 'green') . " Utility will check the groups it belongs to......\n");
}

my $now_u_groups = qx/id $loginId -Gn /;
if ($? != 0 ){
	exit;
}
my @g_array = split(/\s/, $now_u_groups);

foreach (@targetGroups) {
	$row4p{$_}[0] = 'No';
}
foreach (@g_array) {
	$row4p{$_}[0] = 'Yes';
}
my @lastUserGroups = getLastUserGroups($loginId);

foreach (@targetGroups) {
	$row4p{$_}[1] = 'No';
}
foreach (@lastUserGroups) {
	$row4p{$_}[1] = 'Yes';
}

open(RPTFILE, ">report.$$") or die "Cannot open temporary report file";

# Print out title
printf(RPTFILE "%-12s", "UserId");
printf("%-12s", "UserId");
foreach my $key (sort keys %row4p) {
	printf(RPTFILE "%-12s", $key);
	printf("%-12s", $key);
}
print(RPTFILE "\n");
print("\n");
# Print out line
my $size = keys %row4p;
for (my $i = 0; $i <= $size; $i++ ) {
	printf(RPTFILE "%-12s", "------------");
	printf("%-12s", "------------");
}
print(RPTFILE "\n");
print("\n");

# Print out current status
printf(RPTFILE "%-12s", $loginId."[now]");
printf("%-12s", $loginId."[now]");

foreach my $key (sort keys %row4p) {
	printf(RPTFILE "%-12s","$row4p{$key}[0]");
	printf("%-12s","$row4p{$key}[0]");
}
print(RPTFILE "\n");
print("\n");

# Print out the status from backup
printf(RPTFILE "%-12s", $loginId."[old]");
printf("%-12s", $loginId."[old]");

foreach my $key (sort keys %row4p) {
	if ( !defined $row4p{$key}[1] ) {
		$row4p{$key}[1] = 'N/A';
	}
	printf(RPTFILE "%-12s","$row4p{$key}[1]");
	printf("%-12s","$row4p{$key}[1]");
}
print(RPTFILE "\n");
printf("\n");

# Print out line
for (my $i = 0; $i <= $size; $i++ ) {
	printf(RPTFILE "%-12s", "------------");
	printf("%-12s", "------------");
}
print(RPTFILE "\n");
printf("\n");

close(RPTFILE);

if (defined $email) {
	composeNsendEmail();
}
########################################################################################
#### To collect the user's groups from backup_master.cf
########################################################################################
sub getLastUserGroups {
	my ($userId) = @_;
        my @groups = ();
	my $dir = getcwd;
	if ( -e "$dir/$backup_master" ) {
		open(my $masterfile, "$dir/$backup_master") or 
			die("Could not open file '" . $dir/$backup_master . "': $!");
		foreach my $line (<$masterfile>) {
			chomp($line);
    			if( $line =~ /(\w+)\=.*$userId/ ) {
				#print "$1\n";
				my $gn = $1;
				push (@groups, $gn);
			}
		}
	}
	return @groups;
}
#
#######################################################################################
#### To email out the check report
#######################################################################################
sub composeNsendEmail {
	my $hostname = `hostname`;
	my $this_day = `date`;
	#my $email = "ESBSupport\@fcl.crs";
	my $email = "rwang\@localhost";
	my $to = "$email";
	my $from = "admin\@$hostname";
	my $subject = "Check User-Group Report - $this_day";
	my $message;
	open(my $fh, '<', "report.$$") or die "cannot open file $report.$$"; {
        	local $/;
        	$message = <$fh>;
    	}
    	close($fh);
	open(MAIL, "|/usr/sbin/sendmail -t");
	print MAIL "To: $to\n";
	print MAIL "From: $from\n";
	print MAIL "Subject: $subject\n\n";
	print MAIL $message;
	close(MAIL);
	return;
}
