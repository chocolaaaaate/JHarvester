package com.smk.jharvester.model;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * <p>
 * Represents an entry to track. An entry is made up of the page's base URL; the
 * XPath of the HTML element to track; and, the frequency (in minutes) of
 * updating the value of the tracked element.
 * </p>
 * <p>
 * <p>
 * Uses <a href="http://htmlunit.sourceforge.net/">htmlunit</a> by Gargoyle
 * Software Inc. under Apache License, Version 2.0.
 * </p>
 *
 * @author Sumedh Kanade
 * @version 1.0
 */
public class Entry {

    /**
     * The URL of the page on which the XPath is to be evaluated.
     */
    private URL url;

    /**
     * The XPath of the element of interest on the page located at this.url
     */
    private String xPath;

    /**
     * The frequency in minutes at which the XPath should be re-evaluated. NOTE:
     * {@link Entry} does <em>not</em> do this periodic re-evaluation itself.
     */
    private int updateFrequencyInMinutes;

    /**
     * The value after this.xPath was evaluated for this.url in the most recent
     * setXPath(..) or fetchValue(..) call on this {@link Entry}.
     */
    private String value;

    /**
     * This entry's label
     */
    private String label;

    /**
     * Date and time when this entry was last updated
     */
    private LocalDateTime lastUpdated;

    /**
     * Create an {@link Entry}.
     *
     * @param label           Label of this entry.
     * @param url             The URL of the page on which the XPath is to be evaluated.
     * @param xPath           The XPath of the element of interest on the page located at
     *                        this.url. Invalid XPath values will be rejected and an
     *                        exception will be thrown.
     * @param updateFrequency The frequency in minutes at which the XPath should be
     *                        re-evaluated. NOTE: {@link Entry} does <em>not</em> do this
     *                        periodic re-evaluation itself.
     * @throws FailingHttpStatusCodeException
     * @throws IOException
     */
    public Entry(String label, String url, String xPath, int updateFrequency)
            throws FailingHttpStatusCodeException, IOException {
        this.label = label;
        this.url = new URL(url);
        setXPath(xPath);
        this.updateFrequencyInMinutes = updateFrequency;
    }

    public Object getURL() {
        return this.url;
    }

    public String getXPath() {
        return this.xPath;
    }

    /**
     * Get updateIfDue frequency in minutes.
     *
     * @return updateIfDue frequency in minutes.
     */
    public int getUpdateFrequency() {
        return this.updateFrequencyInMinutes;
    }

    public synchronized void setURL(String urlString) throws MalformedURLException {
        setURL(new URL(urlString));
    }

    public synchronized void setURL(URL url) {
        this.url = url;
    }

    /**
     * Attempts to set the XPath of this {@link Entry}.
     * <p>
     * <p>
     * This method call will cause evaluation this.xPath on the page located at
     * this.url using <a href="http://htmlunit.sourceforge.net/">htmlunit</a> by
     * Gargoyle Software Inc. Used under Apache License, Version 2.0.
     * </p>
     * <p>
     * <p>
     * Uses {@link NicelyResynchronizingAjaxController} to evaluate the XPath
     * after any AJAX on the page has finished its work.
     * </p>
     * <p>
     * <p>
     * Invalid XPath value will be rejected via an exception as per htmlunit
     * behavior. See htmlunit docs for details.
     * </p>
     *
     * @param xPath new XPath
     * @throws FailingHttpStatusCodeException
     * @throws MalformedURLException
     * @throws IOException
     */
    public synchronized void setXPath(String xPath) throws FailingHttpStatusCodeException,
            MalformedURLException, IOException {
        this.xPath = xPath;
        /*
         * Evaluating when XPath is to a) avoid having to make a costly
		 * evaluation each time getValue() is called; and b) reject an invalid
		 * XPath right away.
		 */
        this.value = evaluateXPath();
    }

    /**
     * <p>
     * Evaluates this.xPath on the page located at this.url using
     * <a href="http://htmlunit.sourceforge.net/">htmlunit</a> by Gargoyle
     * Software Inc. Used under Apache License, Version 2.0.
     * </p>
     * <p>
     * <p>
     * Uses {@link NicelyResynchronizingAjaxController} to evaluate the XPath
     * after any AJAX on the page has finished its work.
     * </p>
     *
     * @return String value of the xPath evaluation.
     * @throws FailingHttpStatusCodeException
     * @throws MalformedURLException
     * @throws IOException
     */
    private String evaluateXPath() throws FailingHttpStatusCodeException,
            MalformedURLException, IOException {

        java.util.logging.Logger.getLogger("com.gargoylesoftware")
                .setLevel(Level.OFF);
        WebClient webClient = new WebClient(BrowserVersion.getDefault());
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(false);
        // dealing with any AJAX on the page
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());

        HtmlPage page = webClient.getPage(this.url);
        webClient.close(); // important
        List<HtmlElement> xpathResult = page.getByXPath(this.xPath);
        return xpathResult.get(0).getFirstChild().getTextContent();
    }

    /**
     * Sets the updateIfDue frequency (in minutes).
     *
     * @param updateFrequency new updateIfDue frequency in minutes.
     */
    public synchronized void setUpdateFrequency(int updateFrequency) {
        this.updateFrequencyInMinutes = updateFrequency;
    }

    /**
     * Returns the most recent value obtained by evaluating this.xPath on the
     * page at this.url.
     *
     * @return String value of the xPath evaluation.
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Two Entries are equal if and only if their URL and XPath are equal. Equal
     * labels and updateIfDue frequencies are NOT required for equality.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Entry)) {
            return false;
        }
        Entry other = (Entry) obj;
        return this.url.equals(other.getURL())
                && this.xPath.equals(other.getXPath());
    }

    public String getLabel() {
        return this.label;
    }

    public synchronized void setLabel(String newLabel) {
        this.label = newLabel;
    }

    public LocalDateTime getLastUpdated() {
        return this.lastUpdated;
    }

    /**
     * Re-evaluates the value of this entry if due. Re-evaluation is due if time since
     * last update is more than or equal to update frequency.
     *
     * @throws IOException
     */
    public synchronized void updateIfDue() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        if ((this.lastUpdated == null) || now.isAfter(this.lastUpdated.plusMinutes(this.updateFrequencyInMinutes)) ||
                now.isEqual(this.lastUpdated.plusMinutes(this.updateFrequencyInMinutes))) {
            this.value = evaluateXPath();
            this.lastUpdated = now;
        }
    }
}
