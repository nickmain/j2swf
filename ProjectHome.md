UPDATE - June 16, 2011 - I could not escape Flash. I'm finding that I actually need to resurrect and use the j2swf and (maybe j2avm) tools for a commercial project. I don't want to continue writing Java, so I might start adding new code in Clojure, or lift the whole thing up into a model and use code-generation. I'll write a new post when things are figured out.

See http://epistemologicalengineering.blogspot.com/2011/06/update-to-status-of-flash-projects.html

J2SWF is the ongoing maintenance and enhancement project for the JavaSWF codebase.

JavaSWF is a parser and writer for the Flash SWF file format. It includes a reader and writer for AVM2 (ActionScript 3) bytecode.

J2SWF is being maintained in support of the [J2AVM](http://code.google.com/p/j2avm/) project.


---

A major goal of the project is to include the capability of reading and writing the new XFL (XML-based FLA) file format and to implement a compiler from XFL to SWF. (Note - since Adobe released CS4 without XFL export, this goal is on the back-burner).