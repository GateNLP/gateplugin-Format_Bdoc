# Running benchmarks

Some notes and resources for running benchmarks.

## Using the SimpleBenchmark class

Create input directory `bench_in` and output directory `bench_out`

Run:

```
JAVA_TOOL_OPTIONS="-Xmx5G -Xms5G" mvn -Dexec.classpathScope=test test-compile exec:java -Dexec.mainClass="gate.plugin.format.bdoc.benchmarks.SimpleBenchmark" -Dexec.args="bench_in bench_out"
```
