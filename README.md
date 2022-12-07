# sysdig-parser

## How to run

Run `mvn clean package`

## After jar files are created, 

To parse logs and build .dot file:

`java -jar target/sysdig-parser-1.0-jar-with-dependencies.jar <logfile> <output-file>`

To parse logs and run backtrack algorithm using a startVertex and endVertex:

`java -jar target/sysdig-parser-1.0-jar-with-dependencies.jar <logfile> <output-file> <startVertex> <endVertex> <backtrack-output-file>`

Example: `java -jar target/sysdig-parser-1.0-jar-with-dependencies.jar data/sysdig-logs.txt test.dot 2213firefox /proc/21912/oom_score_adj backtracked.dot`
