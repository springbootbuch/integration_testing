# integration_testing

[![Build Status](https://travis-ci.org/springbootbuch/integration_testing.svg?branch=master)](https://travis-ci.org/springbootbuch/integration_testing)

Demonstrate how to use [`docker-maven-plugin`](https://github.com/spotify/docker-maven-plugin) to fire up a MongoDB container to be used during integration tests. 

Note: With Spring Boot 1.5 you could use `@DataMongoTest` with the embedded MongoDB, but this is not the point here.

First: Configure `docker-maven-plugin` with the containers you need, here: mongodb:

```xml
<plugin>
    <groupId>io.fabric8</groupId>
    <artifactId>docker-maven-plugin</artifactId>
    <version>0.20.1</version>
    <configuration>
        <!-- Those are the containers you want to run
            no need to configure ports yet -->
        <images>
            <image>
                <name>mongo:3.2.9</name>
                <alias>mongo-db</alias>
                <run>
                    <!-- Tells the plugin to wait for a log entry for 60s max. -->
                    <wait>
                        <log>waiting for connections on port 27017</log>
                        <time>60000</time>
                    </wait>
                </run>
            </image>
        </images>
    </configuration>
    <!-- Configure the execution of the plugin itself -->
    <executions>
        <execution>
            <id>prepare-it-database</id>
            <!-- During this phase… -->
            <phase>pre-integration-test</phase>
            <!-- …run the start goal -->
            <goals>
                <goal>start</goal>
            </goals>
            <configuration>
                <images>
                    <image>
                        <alias>mongo-db</alias>
                        <run>
                            <!-- Here we need to configure the ports, the 
                                 Mongo image offers 27018, we tell the plugin
                                 to map it onto a random local free port -->
                            <ports>
                                <!-- Maps the random port
                                     onto a Maven property -->
                                <port>mongo-db-it.port:27017</port>
                            </ports>
                        </run>
                    </image>
                </images>
            </configuration>
        </execution>
        <execution>
            <id>remove-it-database</id>
            <!-- Stop the containers after integration test -->
            <phase>post-integration-test</phase>
            <goals>
                <goal>stop</goal>
            </goals>
        </execution>
    </executions>
</plugin>
````

No tests are executed yet. I prefer the `maven-failsafe-plugin` for executing integration tests. `maven-failsafe-plugin` makes sure that a pre and post phase are executed. It also offers the possiblity to push any maven property into the vm environment which we do now with the generated property `mongo-db-it.port` from the `docker-maven-plugin`

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>integration-test</goal>
                <goal>verify</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <environmentVariables>
            <!-- Push the Maven property into the environment under the given name (here: spring.data.mongodb.port) -->
            <spring.data.mongodb.port>${mongo-db-it.port}</spring.data.mongodb.port>
        </environmentVariables>
    </configuration>
</plugin>
```

You can push the whole connectstring into your enviroment. 

As we are using Spring Boot here, the integration test `SomeDocumentRepositoryIT` is pretty simple:

```java
@RunWith(SpringRunner.class)
@DataMongoTest
public class SomeDocumentRepositoryIT {

	private static final Logger LOG = LoggerFactory
		.getLogger(SomeDocumentRepositoryIT.class);

	@Autowired
	private SomeDocumentRepository someDocumentRepository;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Test
	public void doSomething() {
		someDocumentRepository.deleteAll();
		final SomeDocument d = someDocumentRepository.save(new SomeDocument("foobar"));
		LOG.info("Stored document with id {}", d.getId());
		final SomeDocument d2 = mongoTemplate.findById(d.getId(), SomeDocument.class);
		assertThat(d2.getValue(), is("foobar"));
	}
}
```

The property `spring.data.mongodb.port` is automatically picked up from the enviroment with higher priority than configuration files so the test runs agains our container.