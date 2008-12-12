#!/usr/bin/perl

open (ORIGINAL, $ARGV[0]) || die("Could not open file!");
@original = <ORIGINAL>;
close(ORIGINAL);

open (TESTS, $ARGV[1]) || die("Could not open file!");
@tests = <TESTS>;
close(TESTS);

sub getMethodName
{
	($methodHead) = @_;
	$methodHead =~ m/^[\s\t]*(.*)[\s\t]+(.*)[\s\t]*\(.*\)/;
	return $2;
}

sub removePercent
{
	$header = shift;
	$header =~ m/^[\s\t]*(.*[\s\t]+.*[\s\t]*\(.*\))[\s\t]*(1*\.[0-9]+)/;
	return $1."\n";
}

#both pre and post test methods are generated with this subroutine, so only edit this method
sub createTestMethod
{
	$methodName = shift;
	$testMethodName = shift;
	$argList = shift;
	$percent = shift;
	$retMethod = "\t" . "
				if (should_run_test(\"" . $methodName . "\", " . $percent . ") == 0) 
				{ 
					struct timeval startTime, endTime, result;	
					gettimeofday(&startTime, NULL);
					
					increaseTests();
					pthread_t thread;
		
					int mypipe[2];
					pipe(mypipe);
		
					pthread_create(&thread, NULL, (void*)getThread(), (void *) mypipe[0]);
		
					int pid = fork();
					
					if (pid == 0) 
					{ 
						setAffinity(1);
						" . $testMethodName ."(" . $argList . "); 
						close(mypipe[0]);
						write_to_pipe(mypipe[1]); 
						exit(0); 
					} 
					else 
					{ 					
						close(mypipe[1]);
						signal(SIGCHLD, SIG_IGN); 
					}
					
					gettimeofday(&endTime, NULL);
					timeval_subtract(&result, &endTime, &startTime);
					updateTotalTestTime(result);
				}\n";
	return $retMethod;
}


#pull out the includes to make sure we can add our own includes together as a group at the top
$includes = "";
while($k < scalar(@original))
{
	$line = $original[$k];
	if($line =~ m/#include.*/)
	{
		$includes .= $line;
		$original[$k] = "";
	}
	$k++;
}


#run through the user's file of test methods
$i = 0;
while($i < scalar(@tests))
{
	$line = $tests[$i];
	chomp($line);
	if($line =~ m/(.*) (pre|post|both)[\s\t]*$/)
	{
		$functionHead = $1;
		$testTypes = $2;

		print "method[ " . $functionHead . " ] : testTypes[ " . $testTypes . " ]\n";
		#print getMethodName($functionHead) . "\n";

		if($testTypes eq "pre")
		{
			$i++;
			$line = $tests[$i];
			while($line !~ m/(void.*)[\s\t]*(1*\.[0-9]+)/)
			{
				$i++;
				$line = $tests[$i];
			}
			$preMethod = removePercent($line);
			$preMethodHead = $line;
			
			$parenCount = 0;
			$i++;
			$line = $tests[$i];
			do
			{
				while($line =~ /\{/g)
				{
					$parenCount++;
				}
				
				while($line =~ /}/g)
				{
					$parenCount--;
				}				

				$preMethod .= $line . "\n";
				$i++;
				$line = $tests[$i];
			}while($parenCount != 0);
			
			#print $preMethod . "\n";
			$i++;
			
		}
		
		elsif($testTypes eq "post")
		{
			$i++;
			$line = $tests[$i];
			while($line !~ m/(void.*)[\s\t]*(1*\.[0-9]+)/)
			{
				$i++;
				$line = $tests[$i];
			}
			$postMethod = removePercent($line);
			$postMethodHead = $line;
			
			$parenCount = 0;
			$i++;
			$line = $tests[$i];
			do
			{
				while($line =~ /\{/g)
				{
					$parenCount++;
				}
				
				while($line =~ /}/g)
				{
					$parenCount--;
				}				

				$postMethod .= $line . "\n";
				$i++;
				$line = $tests[$i];
			}while($parenCount != 0);
			
			#print $postMethod . "\n";
			$i++;
			
		}

		elsif($testTypes eq "both")
		{
			$i++;
			$line = $tests[$i];
			while($line !~ m/(void.*)[\s\t]*(1*\.[0-9]+)/)
			{
				$i++;
				$line = $tests[$i];
			}
			$preMethod = removePercent($line);
			$preMethodHead = $line;
			
			$parenCount = 0;
			$i++;
			$line = $tests[$i];
			do
			{
				while($line =~ /\{/g)
				{
					$parenCount++;
				}
				
				while($line =~ /}/g)
				{
					$parenCount--;
				}				

				$preMethod .= $line . "\n";
				$i++;
				$line = $tests[$i];
			}while($parenCount != 0);
			
			#print $preMethod . "\n";
			$i++;
			
			$line = $tests[$i];
			while($line !~ m/(void.*)[\s\t]*(1*\.[0-9]+)/)
			{
				$i++;
				$line = $tests[$i];
			}
			$postMethod = removePercent($line);
			$postMethodHead = $line;
			
			$parenCount = 0;
			$i++;
			$line = $tests[$i];
			do
			{
				while($line =~ /\{/g)
				{
					$parenCount++;
				}
				
				while($line =~ /}/g)
				{
					$parenCount--;
				}				

				$postMethod .= $line . "\n";
				$i++;
				$line = $tests[$i];
			}while($parenCount != 0);
			
			#print $postMethod . "\n";
			$i++;
			
		}

		$functionHead =~ m/^[\s\t]*(.*)[\s\t]+(.*)[\s\t]*\((.*)\)/;
		$returnType = $1;
		$methodName = $2;
		$argList = $3;
		
		
		while($argList =~ s/([a-zA-Z0-9-_]+)[\s\t]+([a-zA-Z0-9-_]+)/$2/g){}
		
		################################
		#this section is where we create a new method based on the original method
		$newMethod = $functionHead . "\n{\n";;
		if($testTypes eq "pre")
		{
			$preMethodHead =~ m/^[\s\t]*(.*)[\s\t]+(.*)[\s\t]*\((.*)\)[\s\t]*(1*\.[0-9]+)/;
			$preMethodName = $2;
			
			$percent = $4;
			#ADDING PRE METHOD
			$newMethod .= createTestMethod($methodName, $preMethodName, $argList, $percent);
			
			$newMethod .= "\t" . $returnType . " retVal = _" . $methodName . "(" . $argList . ");\n";
		}
		elsif($testTypes eq "post")
		{
			$postMethodHead =~ m/^[\s\t]*(.*)[\s\t]+(.*)[\s\t]*\((.*)\)[\s\t]*(1*\.[0-9]+)/;
			$postMethodName = $2;
			$percent = $4;
			$newMethod .= "\t" . $returnType . " retVal = _" . $methodName . "(" . $argList . ");\n";
			$argList .= ", retVal";
			
			#ADDING POST METHOD
			$newMethod .= createTestMethod($methodName, $postMethodName, $argList, $percent);
		} 
		elsif($testTypes eq "both")
		{
			
			$preMethodHead =~ m/^[\s\t]*(.*)[\s\t]+(.*)[\s\t]*\((.*)\)[\s\t]*(1*\.[0-9]+)/;
			$preMethodName = $2;
			$percent = $4;
			
			#ADDING PRE METHOD
			$newMethod .= createTestMethod($methodName, $preMethodName, $argList, $percent);
			
			$newMethod .= "\t" . $returnType . " retVal = _" . $methodName . "(" . $argList . ");\n";
			$argList .= ", retVal";
			
			
			$postMethodHead =~ m/^[\s\t]*(.*)[\s\t]+(.*)[\s\t]*\((.*)\)[\s\t]*(1*\.[0-9]+)/;
			$postMethodName = $2;
			$percent = $4;
			#ADDING POST METHOD
			$newMethod .= createTestMethod($methodName, $postMethodName, $argList, $percent);
		}
		$newMethod .= "\t return retVal;\n}\n";
		#################################
		

		#replacing headers
		$j = 0;
		$augment = 0;
		while($j < scalar(@original))
		{
			$line = $original[$j];

			$lookFor = $functionHead;
			$lookFor =~ s/^[\s\t]*(.*)[\s\t]+(.*)[\s\t]*\((.*)\)/$1 $2\(\.\)$3\(\.\)/;

			if($line =~ m/$lookFor$/ && $1 eq "(" && $2 eq ")")
			{	
				$line =~ s/$methodName/_$methodName/;
				$original[$j] = $line;
				print $line . "\n";
			
				$j++;
				$line = $original[$j];
				do
				{
					while($line =~ /\{/g)
					{
						$parenCount++;
					}
				
					while($line =~ /}/g)
					{
						$parenCount--;
					}				
					$j++;
					$line = $original[$j];
				}while($parenCount != 0);
				
				if($testTypes eq "pre")
				{
					$line .= "\n" . $preMethod . "\n" . $newMethod . "\n";
				}
				elsif($testTypes eq "post")
				{
					$line .= "\n" . $postMethod . "\n" . $newMethod . "\n";
				}
				elsif($testTypes eq "both")
				{
					$line .= "\n" . $preMethod . "\n" . $postMethod . "\n" . $newMethod . "\n";
				}
				$original[$j] = $line;
			}
			$j++;
		}

	}
	else
	{
		$i++;
	}
}

open(OUTPUT, ">output.c");

# some headers that we need
print OUTPUT "#include <stdlib.h>\n";
print OUTPUT "#include <signal.h>\n";
print OUTPUT "#include <pthread.h>\n";
print OUTPUT "#include <unistd.h>\n";
print OUTPUT "#include <sys/types.h>\n";
print OUTPUT $includes;
print OUTPUT "#define  __USE_GNU\n";

 
#print OUTPUT "extern void *waitForFinishedTest(void *file);\n";


foreach $line (@original)
{
	print OUTPUT $line;
}

close(OUTPUT);
