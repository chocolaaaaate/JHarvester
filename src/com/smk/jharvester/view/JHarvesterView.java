/**
 * 
 */
package com.smk.jharvester.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.table.TableModel;

import com.google.gson.GsonBuilder;
import com.smk.jharvester.controller.EntriesController;

/**
 * The View in the MVC design pattern. Constructs and displays the main frame of
 * this application's GUI.
 * 
 * @author Sumedh Kanade
 *
 */
public class JHarvesterView implements Observer {

	private EntriesController controller;
	private JFrame mainFrame;
	private EntriesTableModel entriesTableModel;

	public JHarvesterView(EntriesController controller) {
		this.controller = controller;
		this.controller.addObserver(this);
		this.entriesTableModel = new EntriesTableModel(controller);
	}

	/**
	 * Initializes and displays the GUI.
	 */
	public void init() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(
							UIManager.getCrossPlatformLookAndFeelClassName());
				} catch (Exception e) {
					e.printStackTrace();
				}

				JFrame.setDefaultLookAndFeelDecorated(true);
				JDialog.setDefaultLookAndFeelDecorated(true);

				// Create and set up the window.
				mainFrame = new JFrame("JHarvester");
				mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				// TODO: Load list of entries from JSON file (using GSON).

				// TODO: Save the current list of entries on close in JSON
				// format.
				mainFrame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						System.out.println(new GsonBuilder().create()
								.toJson(controller.getAllEntries()));
						super.windowClosing(e);
					}
				});

				/*
				 * CENTRAL PANEL - table of entries
				 */
				JTable table = new JTable(entriesTableModel);
				JScrollPane spTable = new JScrollPane(table);
				JPanel pTable = new JPanel();
				pTable.add(spTable);
				spTable.setPreferredSize(new Dimension(950,
						(int) spTable.getPreferredSize().getHeight()));
				int tableWidth = (int) spTable.getPreferredSize().getWidth();

				/*
				 * SOUTH PANEL - status bar
				 */
				JPanel pStatus = new JPanel(new BorderLayout());
				pStatus.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
				JProgressBar pbFetch = new JProgressBar();
				pbFetch.setString("Ready");
				pbFetch.setStringPainted(true);
				pStatus.add(pbFetch, BorderLayout.CENTER);

				/*
				 * NORTH PANEL - for adding new entries.
				 */

				final double STRETCH_FACTOR = 0.074 / 4;

				JLabel lLabel = new JLabel("Label: ");
				JTextField tfLabel = new JTextField("",
						(int) (STRETCH_FACTOR * tableWidth));
				JPanel pLabel = new JPanel();
				pLabel.add(lLabel);
				pLabel.add(tfLabel);

				JLabel lURL = new JLabel("URL: ");
				JTextField tfURL = new JTextField("",
						(int) (STRETCH_FACTOR * tableWidth));
				JPanel pURL = new JPanel();
				pURL.add(lURL);
				pURL.add(tfURL);

				JLabel lXPath = new JLabel("XPath: ");
				JTextField tfXPath = new JTextField("",
						(int) (STRETCH_FACTOR * tableWidth));
				JPanel pXPath = new JPanel();
				pXPath.add(lXPath);
				pXPath.add(tfXPath);

				JPanel pAddEntryFields = new JPanel();

				// TODO: Refactor constants that are used again in button's
				// click event handler
				String[] frequencyOptions = new String[] { " Update Frequency ",
						"Continuous", "Hourly", "Daily", "Weekly" };
				JComboBox<String> jcbUpdateFrequency = new JComboBox<String>(
						frequencyOptions);

				pAddEntryFields.add(pLabel);
				pAddEntryFields.add(pURL);
				pAddEntryFields.add(pXPath);
				pAddEntryFields.add(jcbUpdateFrequency);

				JButton bAddEntry = new JButton("Add");
				bAddEntry.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent ae) {
						SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {

							@Override
							protected void done() {
								pbFetch.setValue(pbFetch.getMinimum());
								pbFetch.setString("Request was successful.");
								pbFetch.setIndeterminate(false);
								bAddEntry.setEnabled(true);
								super.done();
							}

							@Override
							protected Void doInBackground() throws Exception {
								try {
									bAddEntry.setEnabled(false);
									pbFetch.setValue(pbFetch.getMinimum());
									pbFetch.setIndeterminate(true);
									pbFetch.setString(
											"Please wait while your request is being fetched.");

									// TODO: replace default update frequency
									controller.createEntry(tfLabel.getText(),
											tfURL.getText(), tfXPath.getText(),
											getUpdateFrequencyInMinutes());

								} catch (MalformedURLException e) {
									JOptionPane.showMessageDialog(mainFrame,
											"Invalid URL. Please check the URL and try again.",
											"Error", JOptionPane.ERROR_MESSAGE);

								} catch (RuntimeException e) {
									if (e.getMessage().contains(
											"Could not retrieve XPath")) {
										JOptionPane.showMessageDialog(mainFrame,
												"Invalid XPath. Please check the XPath and try again.",
												"Error",
												JOptionPane.ERROR_MESSAGE);
									} else {
										JOptionPane.showMessageDialog(mainFrame,
												"Unable to fetch that request. Please check the URL and XPath and try again.",
												"Error",
												JOptionPane.ERROR_MESSAGE);
									}
								} catch (IOException e) {
									JOptionPane.showMessageDialog(mainFrame,
											"Unable to fetch that request. Please check the URL and XPath and try again.",
											"Error", JOptionPane.ERROR_MESSAGE);
								}
								return null;
							}

							private int getUpdateFrequencyInMinutes() {
								String uf = (String) jcbUpdateFrequency
										.getSelectedItem();

								switch (uf) {
								case "Continuous":
									return 0;
								case "Hourly":
									return 60;
								case "Daily":
									return 60 * 24;
								case "Weekly":
									return 60 * 24 * 7;
								default:
									return 60;
								}
							}

						};

						swingWorker.execute();
					}
				});

				JPanel northPanel = new JPanel(new BorderLayout(5, 5));
				northPanel.add(pAddEntryFields, BorderLayout.CENTER);
				northPanel.add(bAddEntry, BorderLayout.EAST);

				mainFrame.setLayout(new BorderLayout(5, 5));
				mainFrame.add(northPanel, BorderLayout.NORTH);
				mainFrame.add(pTable, BorderLayout.CENTER);
				mainFrame.add(pStatus, BorderLayout.SOUTH);

				mainFrame.pack();
				mainFrame.setResizable(false);
				mainFrame.setLocationRelativeTo(null);
				mainFrame.setVisible(true);
			}
		});
	}

	/**
	 * Propogate update event to underlying {@link TableModel}
	 */
	@Override
	public void update(Observable o, Object arg) {
		this.entriesTableModel.domainModelChanged();
	}

}
