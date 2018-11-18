# governance-toolbox
A toolbox for automation & docker images around metadata governance :

Static metdata

* Schema registry : to store schema & versions

Dynamic metadata

* InfluxDB : TSDB to store PiT (Point In Time) based metadata.

[Archetype](https://github.com/frtu/governance-toolbox/tree/master/archetype)

* avro : generate a base project for avro data model, generate & compile
* plt-kafka : standalone multi modules project to Publish & Consume Kafka
* plt-spark : standalone multi modules project to run Spark

## Maven Plugins

### Schema maven plugins

[<img src="https://img.shields.io/maven-central/v/com.github.frtu.governance/schema-maven-plugin.svg?label=latest%20release%20:%20schema-maven-plugin"/>](https://search.maven.org/#search%7Cga%7C1%7Ca%3A%22schema-maven-plugin%22+g%3A%22com.github.frtu.governance%22)

#### Mojo :pojo2json
Allow to generate an Avro schema based on POJO (Interface or Implementation)

* includePackage : which package to scan
* subtypesOf : filter all the classes that extends the specified interface or parent class
* outputDirectory : specify the folder into which plugin should generate the .avsc files (One per filtered class found).

See [sample project](https://github.com/frtu/governance-toolbox/tree/master/samples/schema-maven-usage).

```XML
<plugin>
    <groupId>com.github.frtu.governance</groupId>
    <artifactId>schema-maven-plugin</artifactId>
    <version>x.y.z</version>
    <executions>
        <execution>
            <phase>process-resources</phase>
            <goals>
                <goal>pojo2avro</goal>
            </goals>
            <configuration>
                <includePackage>tests</includePackage>
                <subtypesOf>tests.pojo.UserInterface</subtypesOf>
                <outputDirectory>${basedir}/target/generated-sources/</outputDirectory>
            </configuration>
        </execution>
    </executions>
</plugin>
```



### Hortonworks Schema-registry

This plugin allow to scan all Avro schema in the project folder /src/main/avro/*.avsc and register it into 'schemaregistry.url'

```XML
<properties>
	<governance-maven-plugin.version>0.3.0</governance-maven-plugin.version>
	<schemaregistry.url>http://localhost:9090</schemaregistry.url>
</properties>

<build>
	<plugins>
		<plugin>
			<groupId>com.github.frtu.governance</groupId>
			<artifactId>schema-registries-maven-plugin</artifactId>
			<version>${governance-maven-plugin.version}</version>
		</plugin>
	</plugins>
</build>
```

### Confluent Schema-registry

This plugin allow to read ONLY specified  Avro schema in the project folder /src/main/avro/*.avsc and register it into 'schemaRegistryUrls'

```XML
<plugin>
    <groupId>io.confluent</groupId>
    <artifactId>kafka-schema-registry-maven-plugin</artifactId>
    <version>4.0.0</version>
    <configuration>
        <schemaRegistryUrls>
            <param>http://192.168.99.100:8081</param>
        </schemaRegistryUrls>
        <subjects>
            <TestSubject000-key>src/main/avro/TestSubject000-Key.avsc</TestSubject000-key>
            <TestSubject000-value>src/main/avro/TestSubject000-Value.avsc</TestSubject000-value>
        </subjects>
    </configuration>
    <goals>
        <goal>register</goal>
    </goals>
</plugin>
```


* [Confluent Maven plugin docs : schema-registry:register](
https://docs.confluent.io/current/schema-registry/docs/maven-plugin.html#schema-registry-register)
* [Confluent Maven plugin java source](
https://github.com/confluentinc/schema-registry/tree/master/maven-plugin/src/main/java/io/confluent/kafka/schemaregistry/maven)

## Docker images
### Confluent Stack (Kafka, REST API, ...)

Here are the different URLs

- Kafka Bootstrap server : broker:9092

In order to make it works, you will need to map 'broker' to 127.0.0.1 in /etc/hosts. You can call the script at [docker-schema-registry.bash](https://github.com/frtu/governance-toolbox/blob/master/schema-registries/docker-schema-registry.bash#L30-L34)

> kafkahost

### Confluent Schema-registry & Landoop UI

- Schema Registry REST API : [http://localhost:8081](http://localhost:8081)
- Schema Registry UI : [http://localhost:8001](http://localhost:8001)

It serves the schema-registry-ui from port 8001.
A live version can be found at [Landoop Demo of schema-registry-ui](https://schema-registry-ui.landoop.com)

* [https://hub.docker.com/r/landoop/schema-registry-ui/](https://hub.docker.com/r/landoop/schema-registry-ui/)
* [https://hub.docker.com/r/confluentinc/cp-schema-registry/](https://hub.docker.com/r/confluentinc/cp-schema-registry/)

FYI find the API specs at : [Confluent Schema Registry APIs](
https://docs.confluent.io/current/schema-registry/docs/api.html)

Attention this require at least Docker Compose 2.1 to allow Docker images start with order & validation.

### Confluent Kafka REST & Landoop Topic UI

- Kafka REST API : [http://localhost:8082](http://localhost:8082)
- Landoop Topic UI : [http://localhost:8002](http://localhost:8002)

### Hortonworks Schema-registry

Using alternative Docker image. For more details : 

* [https://hub.docker.com/r/thebookpeople/hortonworks-registry/](https://hub.docker.com/r/thebookpeople/hortonworks-registry/)

### InfluxDB & Grafana

Using official Docker image. Look at this link for more details on the port : 

* Docker InfluxDB : [https://hub.docker.com/_/influxdb/](https://hub.docker.com/_/influxdb/)
* Docker Grafana : [https://hub.docker.com/r/grafana/grafana/](https://hub.docker.com/r/grafana/grafana/)

## Maven artefact generator

See [Archetype](https://github.com/frtu/governance-toolbox/tree/master/archetype)
