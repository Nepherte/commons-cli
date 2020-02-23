[![GitHub Version](https://img.shields.io/github/tag/Nepherte/commons-cli.svg?label=latest)](https://github.com/Nepherte/commons-cli/releases/latest)
[![Build Status](https://img.shields.io/travis/com/Nepherte/commons-cli/develop.svg)](https://travis-ci.com/Nepherte/commons-cli)

**commons-cli** is a Java library to parse options and arguments passed to a 
command line application. It can also print out help messages and usage 
statements, detailing the interface that is available to the user.

The latest version is **SNAPSHOT**, an unstable build of the **development** 
branch:

- [commons-cli-SNAPSHOT.jar](http://ivy.nepherte.com/com.nepherte/commons-cli/SNAPSHOT/commons-cli-SNAPSHOT.jar)
- [commons-cli-SNAPSHOT-javadoc.jar](http://ivy.nepherte.com/com.nepherte/commons-cli/SNAPSHOT/commons-cli-SNAPSHOT-javadoc.jar)
- [commons-cli-SNAPSHOT-sources.jar](http://ivy.nepherte.com/com.nepherte/commons-cli/SNAPSHOT/commons-cli-SNAPSHOT-sources.jar)

Formats
-------

This library currently supports a single option format, but more are on the way.
It also has extension points built-in and provides the necessary tools to easily
add your own. Currently supported and planned formats are:

- GNU format (e.g. -v --output=destination)
- POSIX\* format (e.g. -v -o destination)

_\* These formats are currently under development. Watch this project on Github
to get notified of future updates._ 

Usage
-----

Each application has to describe the options and arguments it provides. This 
section explains in more detail how you can define your own command line 
interface, and subsequently parse the tokens passed to your application.

#### Creating option templates

Options are generally used to configure the behavior of an application at 
startup. This library lets you define the options that your application offers 
in a fluent, chained fashion.

To describe an option, use an _Option.Template.Builder_, acquired via 
_Option.newTemplate()_. As part of its definition, you can specify a name, 
description, how many values it accepts, and so on...

The example below creates the _-A_ option template of the _ls_ command:

```java
Option.Template almostAll =
  Option.newTemplate()
    .shortName("A")
    .longName("almost-all")
    .description("do not list implied . and ..")
    .build();
```

The example below creates the single-valued _--block-size_ option template of 
the _ls_ command:

```java
Option.Template blockSize =
  Option.newTemplate()
    .longName("block-size")
    .description("use SIZE-byte blocks")
    .minValue(1).maxValue(1)
    .valueName("SIZE")
    .build()
```

#### Creating command descriptors

The command line descriptor is the aggregation of all available options and the 
number of arguments one can pass to the application. Any token provided by the 
user to the command, will be parsed against this descriptor.

To describe a command, use a _Command.Descriptor_, acquired via 
_Command.newDescriptor()_. As part of its definition, you can specify the name 
of the application, its options and the number of arguments it accepts.

The example below creates the _ls_ command descriptor, with the _-A_ and
 _--block-size_ options templates:

```java
Command.Descriptor ls =
  Command.newDescriptor()
    .name("ls")
    .options(almostAll, blockSize)
    .maxArgs(Integer.MAX_VALUE)
    .build();
```

#### Parsing command tokens

Having defined the available options and arguments, it's time to parse the 
token provided by the user on the command line and verify they all comply with 
our application's interface. 

To parse the tokens, choose a _Parser_, such as the _GnuParser_. You'll need 
that _Command.Descriptor_ from before too. If any parsing error occurs, use a 
_Formatter_ to report them to the user.

The example below parses the tokens passed to the command according to the GNU
format:

```java
try {
  // By choosing a parser, the format is set.
  Parser parser = new GnuParser(descriptor);
  Command cmd = parser.parse(args);
}
catch (ParseException exception) {
  // Notify the problem to the user.
  System.err.printLn("ERROR: " + exception.getMessage());
  new Formatter().printHelp(descriptor);
}
```

An example of a help message as produced by this library:

    Usage: ls [OPTION]... [FILE]...
    List information about the FILEs (the current directory by default).
    Sort entries alphabetically if none of -cftuSUX nor --sort.

    -a, --all                  do not hide entries starting with .
    -A, --almost-all           do not list implied . and ..
    -b, --escape               print octal escapes for nongraphic characters
        --block-size=SIZE      use SIZE-byte blocks
    -B, --ignore-backups       do not list implied entries ending with ~
    -c                         with -lt: sort by, and show, ctime (time of last
                               modification of file status information)
                               with -l: show ctime and sort by name
                               otherwise: sort by ctime
    -C                         list entries by columns

#### Interrogating commands

Ultimately you'll want to know whether a certain option was passed or not, what
value it has, as well as the arguments the user provided as input. 

To figure out what was entered, use the methods available on `Command`. An
instance of this class is returned by the `Parser` implementations.

The example below verifies whether the user passed the _-A_ option:

```java
if (cmd.hasOption("A")) {
  // do not list implied . and ..
}
```

The example below retrieves the value the user passed to the _--block-size_ 
option:

```java
if (cmd.hasOption("block-size")) {
  String blockSize = cmd.getOptionValue("block-size");
}
```

The example below loops over the arguments of the command:
```java
for (int i = 0; i < cmd.argumentCount(); i++) {
 int arg = cmd.getArgument(i); 
}
```


Credits
-------
This library is inspired by **Apache Commons CLI**. Check out their 
[website](https://commons.apache.org/proper/commons-cli/) for more information.
