# PALMe

A parallelized Language Model

DESCRIPTION

PALMe is a simple Java implementation of a language model. It's goal
was to parallelize specific bottlenecks to make it more suitable for 
large amounts of data.

REQUIREMENTS

- The Hadoop MapReduce and the Google Guava Library
- A corpus stored in a plain text file (.txt)
- A paths.xml declaring paths to files and tasks

A paths.xml should have the following format: 

<?xml version="1.0" encoding="UTF-8"?> // XML Header

<path> // This is for a lexicon (see Indexing.class for more information of its purpose)
	<type>lexicon</type> // Mandatory
	<directory>./rsc/indices/lexicons/lexicon.gz</directory> // Must be a valid directory to a .gz or .txt-file
</path> 

<path>
	<type>frequency</type> // Can be either frequency or probability
	<subtype coding="HEXADECIMAL" n="1">indexing</subtype> // Those attributes are mandatory
	// n determines the n-gram order, the coding the format
	<directory task="read">./rsc/indices/1/hex_indices.gz</directory> // same as above, but with task-attribute
</path>

The task-attribute says whether this path leads to an already existing resource ("read") or is the destination
of a file which is yet to be created ("write").

Some sample files are provided within the repository.

USAGE

Blueprints for uses are found in main.Main: 
Either you use create a LanguageModel-object and use calculate() or calculateParallelized() to compute n-gram
probabilities or you load to them to use getSequenceProbility() and/or evaluateLanguageModel to compute the
perplexity.

LICENSE & COPYRIGHT

This is free and unencumbered software released into the public domain.

Anyone is free to copy, modify, publish, use, compile, sell, or
distribute this software, either in source code form or as a compiled
binary, for any purpose, commercial or non-commercial, and by any
means.

In jurisdictions that recognize copyright laws, the author or authors
of this software dedicate any and all copyright interest in the
software to the public domain. We make this dedication for the benefit
of the public at large and to the detriment of our heirs and
successors. We intend this dedication to be an overt act of
relinquishment in perpetuity of all present and future rights to this
software under copyright law.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

For more information, please refer to <http://unlicense.org>
