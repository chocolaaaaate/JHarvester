package com.smk.jharvester.view;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import com.smk.jharvester.controller.EntriesController;
import com.smk.jharvester.model.Entry;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

/**
 * {@link TableModel} for the {@link JTable} of entries.
 * 
 * @author Sumedh Kanade
 *
 */
public class EntriesTableModel extends AbstractTableModel {

	private EntriesController controller;

	public EntriesTableModel(EntriesController controller) {
		this.controller = controller;
	}

	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public String getColumnName(int c) {
		switch (c) {
		case 0:
			return "Label";
		case 1:
			return "Value";
		case 2:
			return "URL";
		case 3:
			return "Update frequency";
		case 4:
			return "Last updated";
		default:
			return null;
		}
	}

	@Override
	public int getRowCount() {
		return controller.getAllEntries().size();
	}

	@Override
	public String getValueAt(int r, int c) {
		Entry e = controller.getAllEntries().get(r);

		switch (c) {
		case 0:
			return e.getLabel();
		case 1:
			return e.getValue();
		case 2:
			return e.getURL().toString();
		case 3:
			return getUpdateFrequencyString(e.getUpdateFrequency());
		case 4:
			return e.getLastUpdated() == null ? "" : e.getLastUpdated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		default:
			return null;
		}
	}


	private static String getUpdateFrequencyString(int updateFreq) {
		switch (updateFreq) {
			case 0:
				return "Continous";
			case 30:
				return "Half-hourly";
			case 60:
				return "Hourly";
			case 60 * 24:
				return "Daily";
			case 60 * 24 * 7:
				return "Weekly";
			default:
				return "" + updateFreq;
		}
	}

	/**
	 * Fires table data changed event, causing the JTable to be updated.
	 */
	public void domainModelChanged() {
		fireTableDataChanged();
	}

}
