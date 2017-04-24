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

    public void startUpdateThread() {
        Runnable timerTask = new Runnable() {
            @Override
            public void run() {
                synchronized (entries) {
                    System.out.println("in synchronized block to updateIfDue all entries");
                    for (Entry entry : entries) {
                        try {
                            entry.updateIfDue();
                            setChanged();
                            notifyObservers();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        };
        ScheduledExecutorService executor =
                Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(timerTask, 1, 10, TimeUnit.SECONDS);
    }


    public List<Entry> getAllEntries() {
        return this.entries;
    }

    public Entry createEntry(String label, String url, String xPath,
                             int updateFrequency)
            throws FailingHttpStatusCodeException, IOException {

        synchronized (entries) {
            System.out.println("in synchronized block to create entry");
            Entry newEntry = new Entry(label, url, xPath, updateFrequency);
            this.entries.add(newEntry);
            setChanged();
            notifyObservers();
            return newEntry;
        }
    }

    public Entry deleteEntry(int index) {
        synchronized (entries) {
            System.out.println("in synchronized block to delete entry");
            Entry removed = this.entries.remove(index);
            setChanged();
            notifyObservers();
            return removed;
        }
    }

}
