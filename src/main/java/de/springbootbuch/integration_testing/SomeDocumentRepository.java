package de.springbootbuch.integration_testing;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Part of springbootbuch.de.
 * @author Michael J. Simons
 * @author @rotnroll666
 */
public interface SomeDocumentRepository extends MongoRepository<SomeDocument, String> {
}