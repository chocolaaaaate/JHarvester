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
	 * TODO: externalize strings constants such as error messages
	 * 
	 * TODO: updating of an existing entry, via right-click menu on entry table
	 * row
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
	 * Starts the application by initializing the View and Controller.
	 * 
	 * @param args
	 *            unused.
	 */
	public static void main(String[] args) {
		EntriesController controller = new EntriesController();
		JHarvesterView view = new JHarvesterView(controller);
		view.init();
	}

}
