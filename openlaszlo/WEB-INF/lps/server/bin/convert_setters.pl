#!/usr/bin/perl
# Copyright 2008-2009 Laszlo Systems.  Use according to license terms.
#
# convert_setters.pl
# Convert deprecated setX calls to setAttribute('X',...) calls
#
# This is not part of convert_required.pl because the conversion may not be
# perfect. For example, basecombobox.lzx defines a setText() method which is
# not an override from lz.text.
#
# Warnings:
#
#   - PLEASE BACK UP your entire work directory before starting.
#
#   - Please compare the end result of each changed file with the
#     version before changing (filename.bak) and verify that the
#     changes make sense.
#
# Should work with Perl 5 or greater.
# tested on Windows using ActiveState perl
# Adapated from convert_required.pl by Don Anderson


use File::Basename;
use File::Copy;
use Getopt::Std;

################
my $VERSION = "1.0.0";
my $PROG = basename($0);
my $USAGE = <<END;
Usage: perl convert_setters.pl [ options ] filename...

Options:

   -d debuglevel
           get debugging info for development

   -g graphical-difftool
           for each file changed, invoke the graphical difftool,
           and prompt the user to keep or not keep the changes

   -q
          Quiet mode. Only display files that have been modified.
   -t
           create output for simple tests in /tmp/convtest

   -v
           show version number and exit

For each file, a backup file is made (filename.bak) and the file will
be converted, with the result put into filename.


Examples:

  # Convert all the lzx files in the directory, doing all conversions.
  \$ perl convert_setters.pl *.lzx

END
################

##
# Other global variables
##
my $DEBUGLEVEL=0;      # set to non-zero to get successive amounts of debug out
my $curfile = "unknown file";  # track current file for error messages
my $quietmode=undef;   # Set to a vlue to run quietly.

# Mapping of old setter to attribute name
my %settername = (
# lz.view
    'setLayout' => 'layout',
    'setFontName' => 'font',
    'setResource' => 'resource',
    'setVisible' => 'visible',
    'setVisibility' => 'visibility',
    'setWidth' => 'width',
    'setHeight' => 'height',
    'setOpacity' => 'opacity',
    'setX' => 'x',
    'setY' => 'y',
    'setRotation' => 'rotation',
    'setAlign' => 'align',
    'setXOffset' => 'xoffset',
    'setYOffset' => 'yoffset',
    'setValign' => 'valign',
    'setColor' => 'fgcolor',
    'setResourceNumber' => 'frame',
#   'stretchResource' => 'stretches',
    'setBGColor' => 'bgcolor',
    'setClickable' => 'clickable',
    'setCursor' => 'cursor',
    'setPlay' => 'play',
    'setShowHandCursor' => 'showhandcursor',
    'setAAActive' => 'aaactive',
    'setAAName' => 'aaname',
    'setAADescription' => 'aadescription',
    'setAATabIndex' => 'aatabindex',
    'setAASilent' => 'aasilent',
    'setProxyURL' => 'proxyurl',
    'setContextMenu' => 'contextmenu',
# lz.text
    'setResize' => 'resize',
    'setMaxLength' => 'maxlength',
    'setPattern' => 'pattern',
    'setScroll' => 'scroll',
    'setXScroll' => 'xscroll',
    'setYScroll' => 'yscroll',
    'setHScroll' => 'hscroll',
    'setText' => 'text',
    'setSelectable' => 'selectable',
    'setFontSize' => 'fontsize',
    'setFontStyle' => 'fontstyle',
    'setAntiAliasType' => 'aliasType',
    'setGridFit' => 'gridFit',
    'setSharpness' => 'sharpness',
    'setThickness' => 'thickness',
# lz.inputtext
    'setEnabled' => 'enabled',
# lz.animator
    'setMotion' => 'motion',
    'setTo' => 'to',
# lz.animatorgroup
    'setTarget' => 'target',
    'setDuration' => 'duration',
# lz.node
    'setDatapath' => 'datapath',  # Also used in LzReplicationManager
# lz.dataset
    # 'setData' => 'data',  # Used in dataset/datatext but 2 args in dataset
    'setSrc' => 'src',
    'setProxyRequests' => 'proxied',
    'setRequest' => 'request',
    'setAutorequest' => 'autorequest',
    'setPostBody' => 'postbody',
    'setQueryType' => 'querytype',
    'setInitialData' => 'initialdata',
# lz.command
    'setKeys' => 'key',
# lz.DataElementMixin
    'setAttrs' => 'attributes',
    'setChildNodes' => 'childNodes',
    'setNodeName' => 'nodeName',
# lz.DataNodeMixin
    'setOwnerDocument' => 'ownerDocument',
# lz.DataText
    # 'setData' => 'data', # see lz.dataset
# lz.state
    'setApply' => 'applied',
# lz.contextmenu
    'setDelegate' => 'delegate',
# lz.contextmenuitem
    # 'setDelegate' => 'delegate', # see lz.contextmenu
    # 'setEnabled' => 'enabled', # see lz.inputtext
    # 'setVisible' => 'visible', # see lz.view
    'setCaption' => 'caption',
    'setSeparatorBefore' => 'separatorbefore'
);


# Search an array for the string and returns 1 if found. Else undef
# search_array($arrayref, $string)
sub search_array
{
  my ($arrayref, $string) = @_;
  return undef unless defined($arrayref);

  foreach my $s (@$arrayref) {
      if ($s eq $string) {
	  return 1;
      }
  }

  return undef;
}


# initialize_substitutions(\@ignore)
#
# Take the information from %settername and construct a hash with the
# actual substitutions to make.
#   replace '.setter(' with 'setAttribute( 'attribute', '
#   replace '.setter (' with 'setAttribute( 'attribute', '
#
# If an arrayref is supplied, these setter names are not overridden in the
# file. This reduces the chance of making a mistake.

my %substitutions = ();
sub initialize_substitutions
{
  my $ignore = shift(@_);
  %substitutions = ();

  while (my ($setter, $attribute) = each(%settername)) {
    # Don't process setters in our ignore list
    if (defined search_array($ignore, $setter)) {
      next;
    }

    # We replace '.setter(' or '.setter ('
    my $setter1 = "\\.$setter\\s*\\(\\s*";
    my $replacement = ".setAttribute('$attribute', ";

    # print "'$setter1', '$replacement\n";

    $substitutions{$setter1} = $replacement;
  }
}


##
# debug(level, string);
# debugln(level, string);
# Show the string if the level is less or equal to
# the current debug level. debug(1, '...') is more likely
# to appear, and debug(9, '...') least likely.
##
sub debug {
    my $level = $_[0];
    my $str = $_[1];
    if ($level <= $DEBUGLEVEL) {
        print STDOUT $str;
    }
}
sub debugln {
    debug($_[0], $_[1] . "\n");
}

##
# debugentry(level, funcname, @_);
# Show the function entry with args if level is <= current debug level.
##
sub debugentry {
    my $n = $#_;
    my $argstr = "";
    my $i = 2;
    while ($i <= $n) {
        if ($i != 2) {
            $argstr .= ", ";
        }
        $argstr .= $_[$i];
        $i++;
    } 
    debugln($_[0], $_[1] . "(" . $argstr . ")");
}

##
# warning(string)
# Show a warning to user.
##
sub warning {
    print STDERR "$curfile: Warning: " . $_[0] . "\n";
}

##
# create_test(filename);
# Put a test file into place.
##
sub create_test {
    my $file = $_[0];

    unlink($file);
    open OUT, ">$file" || die("Cannot create $file");

    my @lines = (
		 "<!-- file to convert -->\n",
		 "this.setWidth(w);\n",
		 "this.setWidth (w);\n",
		 "this.setWidth ( w );\n",
		 "var setWidth;\n",
		 "setWidth(w);\n",
		 "view.setHeight(h);\n"
		 );

    print OUT @lines;
    close OUT;
    print STDOUT "Testing $file\n";
}

##
# file_cannot_exist(filename)
# Complain and die if the file exists.
##
sub file_cannot_exist {
    my $file = $_[0];
    if ( -f "$file" ) {
        print STDERR "$PROG: $file already exists, please rename/remove it and run again\n";
        exit(1);
    }
}

# Compares two array refs of strings. Returns 1 if equal, 0 if not equal
sub compare_arrays {
    my ($ref1, $ref2) = @_;
    return 0 unless @$ref1 == @$ref2;
    for (my $i=0; $i<@$ref1; $i++) {
        return 0 if $ref1->[$i] ne $ref2->[$i];
    }
    return 1;
}


##
# convert_file(filename)
# Do all conversions for the file.
##
sub convert_file {
    my $file = $_[0];
    my $inmethod=0;
    my $event;
    my $name;

    debugentry(1, "convert_file", @_);
    $curfile = $file;
    if (!defined $quietmode && -f "$file.bak") {
	print "WARNING: Skipping $file because $file.bak already exists\n";
	return;
    }
    copy("$file", "$file.bak") || die("Cannot copy to $file.bak");
 
    # Read the entire file, process it, and write it back
    open(IN, "<$file") || die("Cannot open $file");
    my @lines=<IN>;
    close(IN);

    my @orig = @lines;

    # Get the list of method names defined in this file. Warn the user if these
    # names conflict with our stored names
    my @methods = ();
    my @ignore = ();
    foreach my $line (@lines) {
	if ($line =~ /\<method.*name=\"(.*?)\".*\>/) {
            push @methods, $1;
	}
        # Catch this: lz.embed.iframemanager.setSrc = function(...)
	elsif ($line =~ /.+\.(.+?)\s*\=\s*function/) {
            push @methods, $1;
	}
    }
    foreach my $method (@methods) {
        if (exists $settername{$method}) {
            push @ignore, $method;
	    print "WARNING. Possible Conflict: The method '$method' in $file will not be converted by this script because of a possible name collision. Calls to '$method' are normally converted to setAttribute by this script. Please check this file manually.\n";
	}
    }

    # Make sure the substitutions are initialized
    initialize_substitutions (\@ignore);

    # If the file contains any attribute setters, tell the user to manually
    # check it
    my $contents = join("\n", @lines);
    if ($contents =~ /setter\s*=\s*\"\s*setAttribute\s*\"/) {
      print "$file might contain a recursive attribute setters. Please check your setter=\"...\" entries manually\n";
    }

    # Do each of the substitutions listed in %substitutions
    # map() does exactly what we want
    while (my ($orig, $replacement) = each(%substitutions)) {
	map(s/$orig/$replacement/g, @lines);
    }

    if (compare_arrays(\@orig, \@lines) == 1) {
        # No change made to file. Don't write anything
        if (!defined $quietmode) {
            print "No changes to '$file'. Skipping\n";
        }
        return;
    }

    # Second, convert the files
    unlink("$file.tmp");
    open(OUT, ">$file.tmp") || die("Cannot create $file.tmp");
    print OUT @lines;
    close(OUT);
    move("$file.tmp", "$file") || die("Cannot create $file");

    print STDOUT "Converted $file\n";
}

##
# Main program
# parse arguments and dispatch work to convert_file.
##
my $file;
my %options;
$ok = getopts("qd:tx:vg:", \%options);
if (!$ok) {
    print STDERR "$USAGE";
    exit(1);
}

$DEBUGLEVEL = $options{d} || '0';

if ($options{v}) {
    print STDOUT "$PROG: version $VERSION\n";
    exit(0);
}

if ($options{q}) {
    $quietmode = 1;
}

if ($options{t}) {
#    create_test("/tmp/convtest");
#    convert_file("/tmp/convtest");
    create_test("./convtest");
    convert_file("./convtest");
    exit(0);
}

if ($#ARGV < 0) {
    print STDERR "$USAGE";
    exit(1);
}

my $diffTool = "";
my $confirm = 0;
if ($options{g}) {
    $diffTool = $options{g};
    $confirm = 1;
}

foreach $file (@ARGV) {
    if (! -f $file ) {
        print STDERR "$PROG: $file does not exist, skipping\n";
    }
    else {
        convert_file($file);
        if($confirm) {
                my $isDiff = system("diff -q $file.bak $file");
                if($isDiff != 0) {
                        system("$diffTool $file.bak $file");
                        print STDOUT "Would you like to: [u]se new, [k]eep old, [e]dit new: ";
                        $| = 1;
                        my $todo = <STDIN>;
                        chomp $todo;
                        if($todo eq 'k'){
                                system("rm", $file);
                                system("mv", "$file.bak", "$file");
                        } elsif($todo eq 'e'){
                                system("\$EDITOR $file");
                        }
                } else {
                        print STDOUT "FIles are the same... Continuing\n";
                }
        }

    }
}
