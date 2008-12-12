#!/usr/bin/perl

use warnings;
use strict;

require 'stringdata.pm';

if ( $#ARGV == 0 ) { die "No classes specified to snapshot\n"; }
elsif ( $#ARGV == -1 ) { die "Usage: aj_gen-noinvite <outfile> <field to trace> ...\n"; }

open OUTFILE, ">", $ARGV[ 0 ] or die "Could not open output file '" . $ARGV[ 0 ] . "' for writing\n";

#make these set-able via switches
my $default_obj_vec_size = 50;
my $max_chain_len = 30;

my $tracking_info = get_tracking_info();

# make this a function call depending on classes passed in
my $imports = <<ML;
import java.sql.*;
import java.util.*;

import com.invite.changetracking.*;
import com.invite.drivers.*;
ML

########################################### LOGIC

print_header( $imports, $default_obj_vec_size, $max_chain_len  );
print_tracer_aspects( $tracking_info );
print OUTFILE $Stringdata::footer;

########################################### /LOGIC


# make this accept parameters from command line
sub get_tracking_info {
	my $i;

#	my $classes = {
#       	IntFields => { field1 => 'INT_TYPE', field3 => 'INT_TYPE' },
#       };

	my $classes = {
		HSSFWorkbook => { workbook => 'GENERIC_OBJECT_TYPE' }
	};
        
        return $classes;
}


sub print_header {
	my $import_statements = shift;
	my $default_vec_size = shift;
	my $max_len = shift;
	my $timestamp = localtime();

	print OUTFILE sprintf(
				$Stringdata::header,
				$timestamp,
				$import_statements,
				$default_vec_size,
				$max_len
				);
}


#call with a hash of data on what to track as outlined in a previous comment
sub print_tracer_aspects {
	my $class_info = shift;

	foreach my $class ( keys %$class_info ) {
		print OUTFILE "\n\n\t/********   INSTRUMENTATION FOR CLASS $class   ********/\n\n";
		print OUTFILE "\t// Fields for class $class\n";
		print OUTFILE sprintf(
				      	$Stringdata::fields_per_class,
				      	$class,
					$class,
					$class,
					$class,
					$class
				      );

		print OUTFILE "\n\t// Fields for tracking GENERIC_OBJECTs and recording pseduo values for changes\n";
		while ( my ( $field, $type ) = each ( %{ $class_info->{$class} } ) ) {
			if ( $type eq 'GENERIC_OBJECT_TYPE' ) {
				print OUTFILE sprintf(
						      	$Stringdata::generic_object_tracking_field,
							$class,
							$field
						      );
			}
		}

		print OUTFILE "\n\t// Pointcuts for class $class\n";
		print OUTFILE sprintf(
				      	$Stringdata::pointcuts_per_class,
					$class,
					$class,
					$class,
					$class
				      );

		my @members;
		while ( my ( $field, $type ) = each ( %{ $class_info->{$class} } ) ) {
			if ( $type eq 'GENERIC_OBJECT_TYPE' ) {
				push @members, sprintf(
							$Stringdata::object_checks_per_class,
							$class,
							$field,
							$class,
							$field,
							$field
						);
			}
			else {

				push @members, sprintf(
					       		$Stringdata::field_checks_per_class,
							$field,
							$field,
							$field,
							$field,
							$field,
							$type
					       	);
			}
		}

		my $member_equality_checks =  join( "\n", @members );

		print OUTFILE "\n\t// Joinpoints for class $class\n";
		print OUTFILE sprintf(
				      	$Stringdata::joinpoints_per_class,
					$class,
					$class,
					$class,
					$class,
					$class,
					$class,
					$class,
					$class,
					$class,
					$class,
					$member_equality_checks
				      );
	}
}
