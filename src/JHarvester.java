import java.io.IOException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.smk.jharvester.controller.EntriesController;
import com.smk.jharvester.view.JHarvesterView;

/**
 * <p>
 * An application that allows you to track textual information on a webpage
 * given the page's URL and XPath to the text element of interest on that page.
 * The page can have AJAX calls.
 * 
 * Does not support pages that require authentication or pagination.
 * </p>
 * 
 * <p>
 * Uses <a href="http://htmlunit.sourceforge.net/">htmlunit</a> by Gargoyle
 * Software Inc. under Apache License, Version 2.0.
 * </p>
 * 
 * @author Sumedh Kanade
 * 
 */
public class JHarvester {

	/*
	 * TODO: persist to and read from store (JSON format using GSON library)
	 * 
	 * TODO: externalize strings constants such as error messages
	 * 
	 * TODO: updating and deleting of an existing entry, via right-click menu on
	 * entry table row
	 * 
	 * TODO: add a last updated field to Entry
	 * 
	 * TODO: asynchronous updating of entries as per updateFrequency
	 * 
	 * TODO: disallow duplicate entry creation in EntriesController based on
	 * Entry.equals() method (i.e. only considering url and xpath equality)
	 * 
	 * TODO: add an "update all" button
	 */

	/**
	 * TODO: delete later. Only for setting up demo data during testing.
	 */
	private static final String TEST_LABEL = "EntryLabel";

	/**
	 * TODO: delete later. Only for setting up demo data during testing.
	 */
	private static final String TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL = "file:tests/testwebpage.html";

	/**
	 * TODO: delete later. Only for setting up demo data during testing.
	 */
	private static final String KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE = "/html/body/p[1]";

	/**
	 * Starts the application by initializing the View and Controller.
	 * 
	 * @param args
	 *            unused.
	 */
	public static void main(String[] args) {
		EntriesController controller = new EntriesController();

		// TODO: Delete later
		initializeTestData(controller);

		JHarvesterView view = new JHarvesterView(controller);
		view.init();
	}

	/**
	 * Temporarily populating data at runtime. This will be replaced by data
	 * from a data store such as a JSON file. (TODO)
	 * 
	 * @param controller
	 */
	private static void initializeTestData(EntriesController controller) {
		try {
			controller.createEntry(TEST_LABEL,
					TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL,
					KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE, 30);
			controller.createEntry(TEST_LABEL,
					TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL,
					KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE, 60);
			controller.createEntry(TEST_LABEL,
					TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL,
					KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE, 90);
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
