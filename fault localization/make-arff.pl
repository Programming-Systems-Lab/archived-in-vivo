#!/usr/bin/perl

use warnings;
use strict;

use DBI;
use Getopt::Std;
use Data::Dumper;

die "Usage: ./command <outfile name> <history length>\n" unless $#ARGV + 1 == 2;

open OUTFILE, '>', $ARGV[ 0 ] or die "Cannot open output file: $!";
my $history_len = $ARGV[ 1 ];
my $dbConnection = DBI->connect( 'DBI:mysql:invite:localhost', 'root', 'rootpass' );
my $statement;



# use this as a format string for printing the header,
# formatted strings must be single-quoted, comma-separated strings

my $header_format = <<ML;
\@relation DiffRelation

\@attribute succeeded {true,false}
ML

#my $header_format = <<ML;
#\@relation DiffRelation
#
#\@attribute failed {true,false}
#\@attribute field_value numeric
#\@attribute field_change numeric
#\@attribute field_name {%s}
#ML

my $history_attribute_format = "\@attribute method%s_name {%s}\n";

my $method_names_sql = <<ML;
	SELECT object_type, method_name
	FROM diffinfo
	GROUP BY object_type, method_name
ML

my $fields_sql = <<ML;
	SELECT i.object_type, v.field_name
	FROM diffinfo i, diffvals v
	WHERE i.did = v.did
	GROUP BY i.object_type, v.field_name
ML

write_header();




######################################
############# MAIN LOOP ##############
######################################

my $sql = <<ML;
	SELECT t.tid AS tid, i.did AS did, t.failed AS failed, i.object_type AS object_type, i.method_name AS method_name
	FROM trials t, diffinfo i
	WHERE t.tid = i.tid
	ORDER BY t.tid, i.did
ML

my $sqlFailCount = "SELECT count(*) FROM trials WHERE failed = 1";
my $sqlSucceedCount = "SELECT count(*) FROM trials WHERE failed = 0";

my $sqlFailStmt = $dbConnection->prepare( $sqlFailCount );
my $sqlSucceedStmt = $dbConnection->prepare( $sqlSucceedCount );

$sqlFailStmt->execute();
$sqlSucceedStmt->execute();

my $netFailCount = ( $sqlFailStmt->fetchrow_array() )[ 0 ];
my $netSuccessCount = ( $sqlSucceedStmt->fetchrow_array() )[ 0 ];

$sqlFailStmt->finish();
$sqlSucceedStmt->finish();

$statement = $dbConnection->prepare( $sql );
$statement->execute();

my $limitingValue = $netFailCount < $netSuccessCount ? $netFailCount : $netSuccessCount;

my $count = 0; # number of methods written for current TID
my $last_tid = -1;
my $failCount = 0;
my $successCount = 0;

while ( my $row_hashref = $statement->fetchrow_hashref() ) {

#print Dumper( $row_hashref )."\n";

	if ( $failCount >= $limitingValue && $successCount >= $limitingValue ) { last; }

	#test with history_len + 1 because of the true/false field
         
        if ( $last_tid == -1 ) { $last_tid = $row_hashref->{'tid'}; } 

	if ( $row_hashref->{'tid'} != $last_tid ) {
        	# mark shorter-than-history_len history as unknown
        	if ( $history_len + 1 - $count > 0 ) {	
        		my @missing = ("?") x ( $history_len + 1 - $count );
        		print OUTFILE ",";
        		print OUTFILE join( ",", @missing );
        	}
                                                                         
        	print OUTFILE "\n";
        	$count = 0;
        	$last_tid = $row_hashref->{'tid'};
        }
	elsif ( $count == $history_len + 1 ) { next; }

	my $areBeginningNewLine = $count == 0;
	my $testFailed = $row_hashref->{'failed'};

	# actually write something

	if ( $areBeginningNewLine && $testFailed && $failCount < $limitingValue - 1 ) {
		print OUTFILE "true";
		$failCount++;
	}
	elsif ( $areBeginningNewLine && !$testFailed && $successCount < $limitingValue - 1 ) {
		print OUTFILE "false";
		$successCount++;
	}

	# beginning a new entry and SHOULD NOT PRINT ANYTHING FOR THIS ENTRY
	elsif ( $areBeginningNewLine ) {
		$count = $history_len + 1;
		next;
	}

	else {
		if ( $count != $history_len + 1 ) { print OUTFILE ","; }

		#print out the method names
		$row_hashref->{'method_name'} =~ s/ /_/g;
		$row_hashref->{'object_type'} =~ s/ /_/g;
		print OUTFILE "'$row_hashref->{'object_type'}.$row_hashref->{'method_name'}'";

	}
	
	$count++;
}

$statement->finish();
$dbConnection->disconnect();

print OUTFILE "\n";


########################################
########## HEADER PRINTING #############
########################################

sub write_header
{
	my @methods = ();

	$statement = $dbConnection->prepare( $method_names_sql );
	$statement->execute();
	while ( my ( $method_object, $method_name ) = $statement->fetchrow_array() ) {
		$method_name =~ s/ /_/g;
		$method_object =~ s/ /_/g;
		push @methods, "'$method_object.$method_name'";
	}

#restore the below when handling fields properly
#
#my @fields = ();
#$statement = $dbConnection->prepare( $fields_sql );
#$statement->execute();
#while ( my ( $field_object, $field_name ) = $statement->fetchrow_array() ) {
#	$field_object =~ s/ /_/g;
#	push @methods, "'$field_object.$field_name'";
#}

#print OUTFILE sprintf(
#		$header_format,
#		join( ',', @fields )
#	);

	print OUTFILE $header_format;

	# print an attribute line for each member of the history chain up to length
	foreach ( 1 .. $history_len ) {
		print OUTFILE sprintf(
				$history_attribute_format,
				$_,
				join( ',', @methods )
			);
	}

	print OUTFILE "\n\n\@data\n";
}

