#!/usr/bin/perl -w
###############################################################################
## Created by Richard Wang
## Created on Sep. 14, 2018
## ###########################################################################
use Data::Dumper qw(Dumper);
use Getopt::Long;
use strict;
use warnings;
use Cwd;
#
my $qm_name = $ARGV[0];
if (not defined $qm_name) {
  	die "A Queue Manager name is manadatory.\n";
} else{
	print "Your target Queue Manager Name is: $qm_name\n";
}
my $flag = 0;
my @queueList = ();
open (OUT0, "echo 'dis ql(*) '|runmqsc $qm_name|");

while (<OUT0>) {
    if ( /QUEUE\(/ ) {
		my ($queueName) = /QUEUE\((\S+)\)/;
		push @queueList, $queueName;
	}	
}

foreach (@queueList) {
	my $result = qx { echo "dis ql($_) all " | runmqsc $qm_name };
	if ($result =~ /(AMQ8149\:.*)/ ) {
		my $errorMsg = $1;
		print "Local Queue $_ :\n";
		print "$1\n";
		$flag = 1;
	} 
}

exit $flag;

