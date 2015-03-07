## WIP: Reactive Streams with JDBC

This is a simple example to show how to work with RDBMS in the [Reactive Streams](http://www.reactive-streams.org/) way.

- http://www.reactive-streams.org/
- https://github.com/reactive-streams/reactive-streams-jvm
- http://scalikejdbc.org/

Current status is still working in progress.

This implementation isn't verified by [Reactive Streams TCK](https://github.com/reactive-streams/reactive-streams-jvm/tree/v1.0.0.RC3/tck) yet.

### Examples

[src/test/scala/samples/SimpleQuerySpec.scala](https://github.com/seratch/reactive-streams-jdbc/blob/master/src/test/scala/samples/SimpleQuerySpec.scala)

### How to run examples

```bash
git clone https://github.com/seratch/reactive-streams-jdbc.git
cd reactive-streams-jdbc
sbt "testOnly samples.SimpleQuerySpec"
```

### License

The MIT License

