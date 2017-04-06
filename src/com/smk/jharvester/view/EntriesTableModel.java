package com.smk.jharvester.view;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import com.smk.jharvester.controller.EntriesController;
import com.smk.jharvester.model.Entry;

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
		return 4;
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
			return "Update frequency (mins)";
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
			return new Integer(e.getUpdateFrequency()).toString();
		default:
			return null;
		}
	}

	/**
	 * 
	 */
	public void domainModelChanged() {
		fireTableDataChanged();
	}

}
