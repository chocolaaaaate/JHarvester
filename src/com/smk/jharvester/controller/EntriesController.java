package com.smk.jharvester.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.smk.jharvester.model.Entry;
import com.smk.jharvester.view.JHarvesterView;

/**
 * Controller in the MVC pattern that will manage interactions between the view {@link JHarvesterView}
 * and the model ({@link Entry}).
 * 
 * @author Sumedh Kanade
 *
 */
public class EntriesController extends Observable {

	private List<Entry> entries;

	public EntriesController() {
		this.entries = new ArrayList<>();
	}

	public List<Entry> getAllEntries() {
		return this.entries;
	}

	public Entry createEntry(String label, String url, String xPath,
			int updateFrequency)
			throws FailingHttpStatusCodeException, IOException {

		Entry newEntry = new Entry(label, url, xPath, updateFrequency);
		this.entries.add(newEntry);
		System.out.println(this.countObservers());
		setChanged();
		notifyObservers();
		return newEntry;
	}

	public Entry deleteEntry(int index) {
		Entry removed = this.entries.remove(index);
		setChanged();
		notifyObservers();
		return removed;
	}

}
