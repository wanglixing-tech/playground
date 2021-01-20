#!/usr/bin/perl
#########################################################################################
#### This program is for creating a bk_master.cf file based on the current /etc/group
#### information, which will be the base for checking user-group relationship using 
#### check_user_groups.pl
####
#### Created by Richard Wang
#### Created on 08/18/2018
#########################################################################################

use strict;
use warnings;
use POSIX qw/strftime/;
use Cwd qw(getcwd);
#########################################################################################
#### The target group list must be defined here, which will be the base of creating a
#### bk_master.cf file as a standard reference
#########################################################################################
my $backup_master = "bk_master.cf";
my @targetGroups = ( "apmsvc", "mquser", "mqsupp", "mqadmin", "mqbrkrs", "ibuser", "ibsupp", "ibadmin" );


my $loginId = $ARGV[0];
my @groupIds = ();

# Get all userids from target group list
#

my $dir = getcwd;
if ( -e "$dir/$backup_master" ) {
	print "$dir/$backup_master has already been created, rename it......\n";
	my $timestamp = strftime('%Y-%m-%d',localtime);
	rename ("$dir/$backup_master", "$dir/$backup_master\.$timestamp");
}

open ( OUTPUT, ">$dir/$backup_master" );
my %garray;
while (my ($gname, $gpasswd, $gid, $gmembers) = getgrent()) {
    $garray{$gname} = $gmembers;
}

foreach my $gname (sort keys (%garray)) {
	if ( grep( /^$gname/, @targetGroups ) ) {
		print "target group $gname in current system.\n";
		print OUTPUT "$gname\=$garray{$gname}\n";
	}
}
close (OUTPUT);

