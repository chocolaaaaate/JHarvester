package com.smk.jharvester.tests;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.smk.jharvester.model.Entry;

/**
 * Unit tests for the {@link Entry} class.
 *
 * @author Sumedh Kanade
 */
public class TestEntry {

    private static final String TEST_LABEL = "EntryLabel";

    /**
     * Known result of evaluation of
     * KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE on
     * TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL
     */
    private static final String RESULT_OF_PLAIN_HTML_KNOWN_XPATH_EVALUATION = "Unidentified paragraph";

    /**
     * A local HTML test file that has an AJAX script that modifies the initial
     * HTML of the page.
     */
    private static final String TEST_AJAX_CONTAINING_WEBPAGE_URL = "file:tests/testwebpagewithajax.html";

    /**
     * A local HTML test file containing only plain HTML.
     */
    private static final String TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL = "file:tests/testwebpage.html";
    private static final String KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE = "/html/body/p[1]";

    /**
     * Test {@link Entry}
     */
    private Entry entry;

    @Before
    public void setUp() throws Exception {
        entry = new Entry(TEST_LABEL, TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL,
                KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE, 30);
    }

    @Test
    public void testEquals() {
        try {
            Entry e1 = new Entry(TEST_LABEL,
                    TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL,
                    KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE, 30);
            Entry e2 = new Entry(TEST_LABEL,
                    TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL,
                    KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE, 30);
            assertTrue(e1.equals(e2));
        } catch (FailingHttpStatusCodeException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testEquals_DespiteDifferentUpdateFrequenciesOrLabels() {
        try {
            Entry e1 = new Entry(TEST_LABEL,
                    TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL,
                    KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE, 30);
            Entry e2 = new Entry(TEST_LABEL + "addedendum",
                    TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL,
                    KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE, 60);
            assertTrue(e1.equals(e2));
        } catch (FailingHttpStatusCodeException | IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testConstructor_InvalidURLShouldBeRejected() {
        try {
            entry = new Entry(TEST_LABEL, "this is not a valid url",
                    "//*[@id=\"rso\"]/div[1]/div/div/div/h3/a", 30);
            fail("Should have rejected invalid URL");

        } catch (MalformedURLException e) {
            // expected.
        } catch (FailingHttpStatusCodeException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testConstructor_InvalidXPathShouldBeRejected() {
        try {
            entry = new Entry(TEST_LABEL, "https://www.google.com/#q=java",
                    "not a valid XPath", 30);
            fail("Invalid XPath should have been rejected");

        } catch (RuntimeException e1) {
            assertTrue(e1.getMessage().contains("Could not retrieve XPath"));
        } catch (MalformedURLException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testConstructor_ValidURLAndXPath_XPathShouldBeEvaluatedAndValueAvailable() {
        try {
            entry = new Entry(TEST_LABEL,
                    TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL,
                    KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE, 30);
            assertEquals(RESULT_OF_PLAIN_HTML_KNOWN_XPATH_EVALUATION,
                    entry.getValue());

        } catch (MalformedURLException e) {
            fail(e.getMessage());
        } catch (FailingHttpStatusCodeException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    /**
     * Testing valid URL assignment
     */
    @Test
    public void testGetSetURL_StringArg() {
        try {
            assertEquals(new URL(TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL),
                    entry.getURL());
            entry.setURL("https://www.google.com/#q=.net");
            assertEquals(new URL("https://www.google.com/#q=.net"),
                    entry.getURL());

        } catch (MalformedURLException e1) {
            fail(e1.getMessage());
        }
    }

    @Test
    public void testGetSetLabel() {
        assertEquals(TEST_LABEL, entry.getLabel());
        entry.setLabel("New Label");
        assertEquals("New Label", entry.getLabel());
    }

    /**
     * Testing valid URL assignment
     */
    @Test
    public void testGetSetURL_URLArg() {
        try {
            assertEquals(new URL(TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL),
                    entry.getURL());
            entry.setURL(new URL("https://www.google.com/#q=.net"));
            assertEquals(new URL("https://www.google.com/#q=.net"),
                    entry.getURL());

        } catch (MalformedURLException e1) {
            fail(e1.getMessage());
        }
    }

    /**
     * Testing expected rejection of an invalid URL.
     */
    @Test
    public void testSetURL_MalformedURL() {
        URL url = null;
        try {
            url = new URL(TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL);
            assertEquals(url, entry.getURL());
            entry.setURL("a malformed url!");
            fail("Should have rejected a malformed URL");

        } catch (MalformedURLException e1) {
            assertEquals("URL should not have changed", url, entry.getURL());
        }
    }

    /**
     * Testing get and set for a valid XPath.
     */
    @Test
    public void testGetSetXPath_ValidXPath() {
        try {
            URL url = new URL(TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL);
            entry.setURL(url);
            entry.setXPath(KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE);
            assertEquals(KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE,
                    entry.getXPath());

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Testing that an attempt to set XPath of an {@link Entry} to an invalid
     * value is rejected. This is htmlunit behavior.
     */
    @Test
    public void testGetSetXPath_InvalidXPath() {
        try {
            URL url = new URL(TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL);
            entry.setURL(url);
            entry.setXPath("this is not a valid XPath!");
            fail("Should have rejected invalid XPath");

        } catch (RuntimeException e1) {
            assertTrue(e1.getMessage().contains("Could not retrieve XPath"));
        } catch (MalformedURLException e1) {
            fail(e1.getMessage());
        } catch (IOException e1) {
            fail(e1.getMessage());
        }
    }

    /**
     * Testing getting and setting updateIfDue frequency associated with an
     * {@link Entry}
     */
    @Test
    public void testGetSet_UpdateFrequency() {
        assertEquals(30, entry.getUpdateFrequency());
        entry.setUpdateFrequency(60);
        assertEquals(60, entry.getUpdateFrequency());
    }

    /**
     * Tests that XPath can be evaluated for elements without an associated
     * <em>id</em> attribute.
     */
    @Test
    public void testXPathEvaluation_UnidentifiedElements() {
        try {
            // testing with a local file
            URL url = new URL(TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL);
            entry.setURL(url);

            entry.setXPath(KNOWN_VALID_XPATH_EXAMPLE_FOR_PLAIN_HTML_PAGE);
            assertEquals(RESULT_OF_PLAIN_HTML_KNOWN_XPATH_EVALUATION,
                    entry.getValue());

            entry.setXPath("/html/body/a[2]");
            assertEquals("An unidentified link", entry.getValue());

            entry.setXPath("/html/body/div/p");
            assertEquals("Nested paragraph", entry.getValue());

            entry.setXPath("/html/body/div/a[2]");
            assertEquals("An unidentified nested link", entry.getValue());

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tests that XPath can be evaluated for elements with an associated
     * <em>id</em> attribute.
     */
    @Test
    public void testXPathEvaluation_IdentifiedElements() {
        try {
            // testing with a local file
            URL url = new URL(TEST_PLAIN_HTML_CONTAINING_WEBPAGE_URL);
            entry.setURL(url);

            entry.setXPath("//*[@id=\"p1\"]");
            assertEquals("Identified paragraph", entry.getValue());

            entry.setXPath("//*[@id=\"link1\"]");
            assertEquals("A link", entry.getValue());

            entry.setXPath("//*[@id=\"link2\"]");
            assertEquals("A nested link", entry.getValue());

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * This test file has an asynchronous/XHR call that modifies the DOM. This
     * test will expect XPath to evaluate using the post-AJAX contents of the
     * page.
     */
    @Test
    public void testXPathEvaluation_ByPassingAjax() {
        try {

            URL url = new URL(TEST_AJAX_CONTAINING_WEBPAGE_URL);
            entry.setURL(url);

            entry.setXPath("//*[@id=\"ajaxtest\"]/h1");
            assertEquals("AJAX bypassing", "Updated Title Via AJAX",
                    entry.getValue());

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdate_LastUpdatedNullOnConstruction() {
        assertNull(entry.getLastUpdated());
    }

    @Test
    public void testUpdate_UpdatesLastUpdated() {
        try {
            entry.updateIfDue();
            assertNotNull(entry.getLastUpdated());
        } catch (Exception e) {
            fail();
        }
    }

}
