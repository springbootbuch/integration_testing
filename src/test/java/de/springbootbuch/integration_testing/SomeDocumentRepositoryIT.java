package de.springbootbuch.integration_testing;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Part of springbootbuch.de.
 *
 * @author Michael J. Simons
 * @author @rotnroll666
 */
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
