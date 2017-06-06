package com.smk.jharvester.controller;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.smk.jharvester.model.Entry;
import com.smk.jharvester.view.JHarvesterView;

/**
 * Controller in the MVC pattern that will manage interactions between the view {@link JHarvesterView}
 * and the model ({@link Entry}).
 *
 * @author Sumedh Kanade
 */
public class EntriesController extends Observable {

    private List<Entry> entries;

    public EntriesController() {
        this.entries = new ArrayList<>();
    }

    /**
     * Starts an auto-update thread in the background that checks every 10 seconds if any entries are due for a refresh
     * (i.e. re-fetching data at that entry's URL and XPath). Updates observers of changes.
     * Thread scheduled using the {@link ScheduledExecutorService}
     */
    public void startUpdateThread() {
        Runnable timerTask = new Runnable() {
            @Override
            public void run() {
                synchronized (entries) {
                    for (Entry entry : entries) {
                        try {
                            entry.updateIfDue();
                            setChanged();
                            notifyObservers();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            setChanged();
                            notifyObservers(ex);
                        }
                    }
                }
            }
        };
        ScheduledExecutorService executor =
                Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(timerTask, 1, 10, TimeUnit.SECONDS);
    }


    /**
     * Returns the current list of entries maintained by this controller.
     */
    public List<Entry> getAllEntries() {
        return this.entries;
    }

    /**
     * Create an Entry and add it to the collection of entries maintained by this controller. For parameter information, see @{@link Entry} constructor.
     * @return the created Entry
     * @throws FailingHttpStatusCodeException
     * @throws IOException
     */
    public Entry createEntry(String label, String url, String xPath,
                             int updateFrequency)
            throws FailingHttpStatusCodeException, IOException {

        synchronized (entries) {
            Entry newEntry = new Entry(label, url, xPath, updateFrequency);
            this.entries.add(newEntry);
            setChanged();
            notifyObservers();
            return newEntry;
        }
    }

    /**
     * Delete Entry at the given index.
     * @param index 0 based.
     * @return the deleted Entry.
     */
    public Entry deleteEntry(int index) {
        synchronized (entries) {
            Entry removed = this.entries.remove(index);
            setChanged();
            notifyObservers();
            return removed;
        }
    }

}
