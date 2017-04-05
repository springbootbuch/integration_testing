package de.springbootbuch.integration_testing;

import org.springframework.data.mongodb.core.mapping.Document;


/**
 * Part of springbootbuch.de.
 * @author Michael J. Simons
 * @author @rotnroll666
 */
@Document(collection = "some_documents")
public class SomeDocument {

	private String id;

	private String value;

	public SomeDocument(String value) {
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}