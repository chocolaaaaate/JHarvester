package com.smk.jharvester.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.smk.jharvester.controller.EntriesController;
import com.smk.jharvester.model.Entry;

/**
 * Unit test suite for {@link EntriesController}
 * 
 * @author Sumedh Kanade
 */
public class TestEntriesController {

	/**
	 * Known result of evaluation of
	 * KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE on
	 * TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL
	 */
	private static final String RESULT_OF_PLAIN_HTML_KNOWN_XPATH_EVALUATION = "Unidentified paragraph";

	/**
	 * A local HTML test file containing only plain HTML.
	 */
	private static final String TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL = "file:tests/testwebpage.html";
	private static final String KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE = "/html/body/p[1]";

	private static final String TEST_LABEL = "EntryLabel";
	
	private EntriesController entriesController;

	@Before
	public void setUp() throws Exception {
		entriesController = new EntriesController();
	}

	@Test
	public void testCreateEntries() {
		try {
			// Add an entry
			Entry createdEntry1 = entriesController.createEntry(
					TEST_LABEL, TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL,
					KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE, 30);
			List<Entry> allEntries = entriesController.getAllEntries();
			assertEquals(1, allEntries.size());
			assertEquals(createdEntry1, allEntries.get(0));
			assertEquals(new URL(TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL),
					createdEntry1.getURL());
			assertEquals(KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE,
					createdEntry1.getXPath());
			assertEquals(30, allEntries.get(0).getUpdateFrequency());
			assertEquals(RESULT_OF_PLAIN_HTML_KNOWN_XPATH_EVALUATION,
					createdEntry1.getValue());

			// Add a second entry
			Entry createdEntry2 = entriesController.createEntry(TEST_LABEL, 
					TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL,
					KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE, 60);
			allEntries = entriesController.getAllEntries();
			assertEquals(2, allEntries.size());
			assertEquals(createdEntry2, allEntries.get(1));
			assertEquals(new URL(TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL),
					createdEntry2.getURL());
			assertEquals(KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE,
					createdEntry2.getXPath());
			assertEquals(60, allEntries.get(1).getUpdateFrequency());
			assertEquals(RESULT_OF_PLAIN_HTML_KNOWN_XPATH_EVALUATION,
					createdEntry2.getValue());

		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testDeleteEntries() {
		try {

			Entry e1 = entriesController.createEntry(TEST_LABEL, 
					TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL,
					KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE, 30);

			Entry e2 = entriesController.createEntry(TEST_LABEL, 
					TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL,
					KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE, 60);

			Entry e3 = entriesController.createEntry(TEST_LABEL, 
					TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL,
					KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE, 90);

			// Before delete
			List<Entry> allEntries = entriesController.getAllEntries();
			assertEquals(3, allEntries.size());
			assertTrue(allEntries.containsAll(Arrays.asList(e1, e2, e3)));

			// Delete
			Entry deletedEntry = entriesController.deleteEntry(1);
			
			assertEquals(e2, deletedEntry);

			// After delete
			allEntries = entriesController.getAllEntries();
			assertEquals(2, allEntries.size());
			assertTrue(allEntries.containsAll(Arrays.asList(e1, e3)));

		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}

	@Test
	public void testNotifyObservers_onCreate() {
		final ModifiableInteger numberOfTimesNotified = new ModifiableInteger(
				0);
		Observer observer = new Observer() {

			@Override
			public void update(Observable o, Object arg) {
				numberOfTimesNotified
						.setValue(numberOfTimesNotified.getValue() + 1);
			}
		};

		entriesController.addObserver(observer);
		try {
			entriesController.createEntry(TEST_LABEL, 
					TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL,
					KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE, 30);
			assertEquals(1, numberOfTimesNotified.getValue());
			entriesController.createEntry(TEST_LABEL, 
					TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL,
					KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE, 90);
			assertEquals(2, numberOfTimesNotified.getValue());
		} catch (FailingHttpStatusCodeException | IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testNotifyObservers_onDelete() {
		final ModifiableInteger numberOfTimesNotified = new ModifiableInteger(
				0);
		Observer observer = new Observer() {

			@Override
			public void update(Observable o, Object arg) {
				numberOfTimesNotified
						.setValue(numberOfTimesNotified.getValue() + 1);
			}
		};
		try {
			entriesController.addObserver(observer);
			entriesController.createEntry(TEST_LABEL, 
					TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL,
					KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE, 30);
			assertEquals(1, numberOfTimesNotified.getValue());
			entriesController.createEntry(TEST_LABEL, 
					TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL,
					KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE, 90);
			assertEquals(2, numberOfTimesNotified.getValue());

			entriesController.deleteEntry(0);

			assertEquals(3, numberOfTimesNotified.getValue());
		} catch (FailingHttpStatusCodeException | IOException e) {
			fail(e.getMessage());
		}
	}

	// TODO: updateEntry
	// TODO: updateAllEntries
	// TODO: loadAllEntriesFromStore
	// TODO: getEntriesMatching(url)

	private class ModifiableInteger {
		private int value;

		protected ModifiableInteger(int i) {
			this.value = i;
		}

		protected void setValue(int i) {
			this.value = i;
		}

		protected int getValue() {
			return this.value;
		}
	}
}
