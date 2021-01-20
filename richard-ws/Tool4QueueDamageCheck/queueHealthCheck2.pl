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
my $finalRet = 0;
my $qm_name = $ARGV[0];
if (not defined $qm_name) {
        die "A Queue Manager name is manadatory.\n";
	exit 1;
} else{
        print "Your target Queue Manager Name is: $qm_name\n";
}

open (CHS_OUT, "echo 'dis ql(*) '|runmqsc $qm_name|");

while (<CHS_OUT>) {
    if ( /QUEUE\(/ ) {
        my ($queueName) = /QUEUE\((\S+)\)/;
        print "QN: $queueName\n";
	my $result = qx{ java -jar ./mqbrowse2.jar -m $qm_name -q $queueName };
 	print $result;
	if ( $result =~ /MQ\sexception\:.*RC\s=\s2101/ ) {
		print "The queue object has been damaged.\n";
		$finalRet = 1;
    	}
    }
}
exit $finalRet;

