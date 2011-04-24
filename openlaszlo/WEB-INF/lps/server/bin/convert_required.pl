#!/usr/bin/perl
# Copyright 2008 Laszlo Systems.  Use according to license terms.
#
# convert_required.pl
# Helps to make required conversions of 4.1 lzx files
# that:
#  1) use deprecated form of 'new' and 'instanceof' within scripts,
#     for example,
#        'new LzText' is converted to 'new lz.text'
#        'new myclass' is converted to 'new lz.myclass'
#  2) use old names for services
#     for example,
#         'LzTimer' is converted to 'lz.Timer' (wherever it is seen)
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
# tested on OSX 10.5.3 with perl v5.8.8
# Author: Don Anderson


################
# Conversions:
#
# On script lines that contain references to classes such
# as LzAudio, LzBrowser, ...  (the complete list is in
# convert_class_name_changes()):
#
#  -    change the name from Lz<name> to lz.<name>
#
# On script lines that are of the form 'new classname...' or
# 'instanceof classname...':
#
#   -   if classname already has lz. , no change
#
#   -   if classname is a class/interface defined in one of the input
#       files (e.g. via <class name="classname"...), then
#       change to new lz.classname  (or instanceof lz.classname).
#
#   -  if classname is class for a LFC tagname, convert it to tagname
#      and add lz.  (new LzView => lz.view)
#
#   -   new global[...] is converted to new lz[...]
#
#   -   otherwise, no change (this includes Object, Array, ...).
#

use File::Basename;
use File::Copy;
use Getopt::Std;

################
my $VERSION = "1.0.0";
my $PROG = basename($0);
my $USAGE = <<END;
Usage: perl convert_required.pl [ options ] filename...

Options:

   -d debuglevel
           get debugging info for development

   -g graphical-difftool
           for each file changed, invoke the graphical difftool,
           and prompt the user to keep or not keep the changes

   -t
           create output for simple tests in /tmp/convtest

   -v
           show version number and exit

For each file, a backup file is made (filename.bak) and the file will
be converted, with the result put into filename.


Examples:

  # Convert all the lzx files in the directory, doing all conversions.
  \$ perl convert_required.pl *.lzs

END
################

##
# Other global variables
##
my $DEBUGLEVEL=0;      # set to non-zero to get successive amounts of debug out
my $curfile = "unknown file";  # track current file for error messages

# Conversions from LzName to lz.name, but only when used with 'new'/'instanceof'
# This list is derived from:
#   WEB-INF/lps/server/src/org/openlaszlo/compiler/ClassModel.java
my %lzxname = (
    # global classes
    LzNode                  => "lz.node",
    LzView                  => "lz.view",
    LzText                  => "lz.text",
    LzInputText             => "lz.inputtext",
    LzCanvas                => "lz.canvas",
    LzScript                => "lz.script",
    LzAnimatorGroup         => "lz.animatorgroup",
    LzAnimator              => "lz.animator",
    LzLayout                => "lz.layout",
    LzState                 => "lz.state",
    LzCommand               => "lz.command",
    LzSelectionManager      => "lz.selectionmanager",
    LzDataSelectionManager  => "lz.dataselectionmanager",
    LzDatapointer           => "lz.datapointer",
    LzDataProvider          => "lz.dataprovider",
    LzDatapath              => "lz.datapath",
    LzDataset               => "lz.dataset",
    LzDatasource            => "lz.datasource",
    LzHTTPDataProvider      => "lz.lzhttpdataprovider",
    LzLibrary               => "lz.import",
    LzDelegate              => "lz.Delegate",
    LzParam                 => "lz.Param",
    # service classes
    LzTimer                 => "lz.Timer",
    LzTrack                 => "lz.Track",
    LzGlobalMouse           => "lz.GlobalMouse",
    LzKeys                  => "lz.Keys",
    LzIdle                  => "lz.Idle",
    LzCursor                => "lz.Cursor",
    LzModeManager           => "lz.ModeManager",
    LzInstantiator          => "lz.Instantiator",
    LzFocus                 => "lz.Focus",
    LzBrowser               => "lz.Browser",
    LzHistory               => "lz.History",
    LzAudio                 => "lz.Audio",
    );

# This list of known classes.  We add to it each time
# we see a class definition.

my %lzsclassname = ();


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

    # basic tests, also with '' delimiter
    print OUT
        "<!-- file to convert -->" .
        "<script>new something</script>\n" .
        "<script>new LzView  // want lz.view\n" .
        "   y = new node</script>\n" .
        "<class name=\"myclass\" extends=\"foo\"><script>\n" .
        "do_not_convert = newsome\n\n" .
        "y = n instanceof z\n" .
        "already_converted = instanceof lz.xyz\n" .
        "y = n instanceof Boolean\n" .
        "y = n instanceof LzView // want lz.view\n" .
        "y = n instanceof view\n" .
        "y = LzFocus.stuff // want lz.Focus.stuff\n" .
        "y = LzFocusNomatch.stuff\n" .
        "y = n instanceof myclass // want lz.myclass\n" .
        "y = new myotherclass  // want lz.myotherclass\n" .
        "y = new global[zz]  // want lz[zz]\n" .
        "y = new somethingelse[*]\n" .
        "y = n instanceof LzView  // want lz.view\n" .
        "y = new LzView(new Integer(new LzNode(), new LzView))   // 3 subs\n" .
        "</class>" .
        "y = n instanceof LzParam\n // want lz.Param" .
        "var f=LzFocus.getFocus();  // want lz.Focus" . 
        "<handler name=\"onkeydown\" reference=\"LzKeys\" args=\"keycode\">  // want lz.Keys" . 
        "<class name=\"myotherclass\"/>\n";

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

##
# add_lz_prefix(classnm)
# 
##
sub add_lz_prefix() {
    my $nm = $_[0];
    if (exists $lzxname{$nm}) {
        return $lzxname{$nm};
    }
    if (exists $lzsclassname{$nm}) {
        return 'lz.' . $nm;
    }
    return $nm;                 # false
}

##
# track_class(line)
# Recognize <class> and <interface> statements, and register their names
##
sub track_class {
    my $line = $_[0];

    if ($line =~ /<(class|interface)\s+.*name=(["'\w]+)\b/) {
        # register that we know about this class
        my $classname = $2;
        $classname =~ s/"//g;
        $lzsclassname{$classname} = "";
        debugln(1, "CLASS match: $classname");
    }
}

##
# convert_new_instanceof()
#    'new LzText' => 'new lz.text' 
#          (and 'foo instanceof LzText' => 'foo instanceof lz.text')
#    'new myclass' => 'new lz.myclass'
##
sub convert_new_instanceof {
    while (/(.*)\b(new|instanceof)(\s+)(lz[.])?([_a-zA-Z0-9]+)\b(.*)/) {
        debugln(3, "Line: $_");
        debugln(3, "MATCH: $1, $2, $3, $4, $5, $6");
        my($first, $keyword, $space, $optlz, $classnm, $last) = ($1, $2, $3, $4, $5, $6);
        my $sub = "$optlz$classnm";
        # Special case - handle 'new global[*]' => 'new lz[*]'
        if ($keyword eq 'new' && $optlz eq '' && $classnm eq 'global' &&
            $last =~ /^\s*\[/) {
            debugln(4, "match new global: $last");
            $sub = "lz";
        }
        elsif ($optlz eq '') {
            $sub = &add_lz_prefix($classnm);
        }

        # We insert @ around the keyword so we won't match it again.
        # This allows us to successively match nested
        # (e.g. 'new class1(new class2())') or sequential
        # (e.g. 'new class1(), new class2()') items.
        # We remove the @ after we've applied substitutions to the whole line.
        $_ = "$first\@$keyword\@$space$sub$last";
        debugln(3, "Modified: $_");
    }
    s/\@new\@/new/g;
    s/\@instanceof\@/instanceof/g;
}

##
# convert_class_name_changes()
# Convert LzAudio => lz.Audio  , etc.
##
sub convert_class_name_changes {
    s/\bLz(Audio|Browser|Cursor|Focus|GlobalMouse|History|Idle|Instantiator|Keys|ModeManager|Timer|Track)\b/lz.\1/g;
}

##
# emit_content(HANDLE, string)
# Emits the content to the handle, applying some basic text transforms.
##
sub emit_content {
    my $save = $_;
    my $FH = $_[0];
    $_ = $_[1];
    my $hadnewline = ($_ =~ "\n");

    &convert_class_name_changes();
    &convert_new_instanceof();

    # After matching, the newline may be lost.
    # Add it again here so everything comes out even.
    if ($_ !~ "\n" && "$hadnewline" == 1) {
        $_ .= "\n";
    }

    debugln(3, "   EMIT CONTENT: " . $_);
    print $FH $_ ;
    $_ = $save;
    return $inmethod;
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
    copy("$file", "$file.bak") || die("Cannot copy to $file.bak");

    # Two pass algorithm.
    # First take note of all 'class' declarations
    open(IN, "<$file") || die("Cannot open $file");
    while (<IN>) {
        track_class($_);
    }
    close(IN);

    # Second, convert the files
    open(IN, "<$file") || die("Cannot open $file");
    unlink("$file.tmp");
    open(OUT, ">$file.tmp") || die("Cannot create $file.tmp");
    while (<IN>) {
        emit_content(OUT, $_);
    }
    close(OUT);
    close(IN);
    move("$file.tmp", "$file") || die("Cannot create $file");
    print STDOUT "Converted $file\n";
}

##
# Main program
# parse arguments and dispatch work to convert_file.
##
my $file;
my %options;
$ok = getopts("d:tx:vg:", \%options);
if (!$ok) {
    print STDERR "$USAGE";
    exit(1);
}

$DEBUGLEVEL = $options{d} || '0';

if ($options{v}) {
    print STDOUT "$PROG: version $VERSION\n";
    exit(0);
}

if ($options{t}) {
    create_test("/tmp/convtest");
    convert_file("/tmp/convtest");
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
    if (! -f $file) {
    }
    file_cannot_exist("$file.bak");
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
