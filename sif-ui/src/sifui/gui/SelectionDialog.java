/**
 * 
 */
package sifui.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.xml.bind.JAXBException;

import org.xml.sax.SAXParseException;

import sif.IO.xml.SifMarshaller;
import sif.frontOffice.FrontDesk;
import sif.model.inspection.InspectionRequest;
import sif.model.policy.DynamicPolicy;

/**
 * @author Manuel Lemcke
 * 
 */
public class SelectionDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8713415050053441789L;
	private final int height = 200;
	private final int width = 500;

	private LayoutManager layout = new GridBagLayout();

	/*
	 * GUI Components
	 */
	private JLabel browseSheetsLabel = new JLabel("Spreadsheet: ");
	private JLabel browsePoliciesLabel = new JLabel("Policy: ");
	private JLabel reqNameLabel = new JLabel("Request Name: ");

	private JFileChooser policyChooser = new JFileChooser();
	private JFileChooser sheetChooser = new JFileChooser();
	private JButton browseSheetsButton = new JButton("Durchsuchen...");
	private JButton browsePoliciesButton = new JButton("Durchsuchen...");
	private JButton startButton = new JButton("Start");
	private JTextField reqName = new JTextField();
	private JTextField spreadsheetPathField = new JTextField();
	private JTextField policyPathField = new JTextField();
	private TableModel tableModel = new DefaultTableModel();
	private JTable table = new JTable(tableModel);
	// private JScrollPane scrollPane = new JScrollPane();

	/*
	 * Variables
	 */
	private File chosenSpreadsheet = null;
	@SuppressWarnings("unused")
	private File chosenPolicy;

	public SelectionDialog() {
		String[] sheetExtensions = { ".xls", ".xlst" };
		String[] policyExtensions = { ".xml" };

		GridBagConstraints c = new GridBagConstraints();

		/*
		 * Configure components
		 */
		browseSheetsButton.setSize(100, 50);
		browseSheetsButton.addActionListener(SelectionDialog.this);

		browsePoliciesButton.setSize(100, 50);
		browsePoliciesButton.addActionListener(SelectionDialog.this);

		startButton.setSize(100, 50);
		startButton.addActionListener(SelectionDialog.this);

		reqName.setSize(100, 50);

		String spreadsheetFilterName = "Spreadsheet (";
		for (int i = 0; i < sheetExtensions.length; i++) {
			String string = sheetExtensions[i];
			spreadsheetFilterName += string;
			if (i < sheetExtensions.length - 1) {
				spreadsheetFilterName += ", ";
			}
		}
		spreadsheetFilterName += ")";
		FileFilter filter = new ExtensionFilter(spreadsheetFilterName,
				sheetExtensions);
		sheetChooser.addChoosableFileFilter(filter);
		sheetChooser.setFileFilter(filter);

		FileFilter xmlFilter = new ExtensionFilter("XML File (.xml)",
				policyExtensions);
		policyChooser.addChoosableFileFilter(xmlFilter);
		policyChooser.setFileFilter(xmlFilter);

		table.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));

		/*
		 * Configure Dialog and add components
		 */
		this.setLayout(layout);
		this.setSize(width, height);
		

		c.gridx = 0;
		c.gridy = 0;
		this.getContentPane().add(browsePoliciesLabel, c);
		

		c.gridx = 1;
		c.gridy = 0;
		this.getContentPane().add(browsePoliciesButton, c);
		
		c.gridx = 2;
		c.gridy = 0;
		policyPathField.setPreferredSize(new Dimension(200, 25));
		policyPathField.setEditable(false);
		this.getContentPane().add(policyPathField, c);		
		
		c.gridx = 0;
		c.gridy = 1;
		this.getContentPane().add(browseSheetsLabel, c);
		
		c.gridx = 1;
		c.gridy = 1;
		this.getContentPane().add(browseSheetsButton, c);
		
		c.gridx = 2;
		c.gridy = 1;
		spreadsheetPathField.setPreferredSize(new Dimension(200, 25));
		spreadsheetPathField.setEditable(false);
		this.getContentPane().add(spreadsheetPathField, c);


		c.gridx = 0;
		c.gridy = 2;
		this.getContentPane().add(reqNameLabel, c);

		c.gridx = 1;
		c.gridy = 2;
		reqName.setPreferredSize(new Dimension(110, 25));
		this.getContentPane().add(reqName, c);

		c.gridx = 1;
		c.gridy = 3;
		this.getContentPane().add(startButton, c);

		// table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		// scrollPane.add(table);
		// c.gridx = 0;
		// c.gridy = 2;
		// c.gridwidth = 3;
		// c.gridheight = 3;
		// c.weightx = 1;
		// c.weighty = 0.5;
		// c.fill = GridBagConstraints.BOTH;
		// this.getContentPane().add(scrollPane, c);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource().equals(browseSheetsButton)) {
			openSheetsChooser();
		} else if (arg0.getSource().equals(browsePoliciesButton)) {
			openPoliciesChooser();
		} else if (arg0.getSource().equals(startButton)) {
			runSif();
		}
		// SelectionDialog.this.setVisible(false);
	}

	private void runSif() {
		DynamicPolicy policy;
		try {
			policy = SifMarshaller.unmarshal(this.chosenPolicy);

			FrontDesk office = FrontDesk.getInstance();

			try {
				office.createAndRunDynamicInspectionRequest(reqName.getText(),
						this.chosenSpreadsheet, policy);
				office.createInspectionReport(this.chosenPolicy.getParent());
				JOptionPane.showMessageDialog(
						this,
						"A report was created at "
								+ this.chosenPolicy.getParent() + File.separator
								+ reqName.getText() + ".html");
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this,
						"The test could not be executed: " + e.getMessage());
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(
					this,
					"An error has occured during unmarshalling: "
							+ e.getMessage());
		}

	}

	private void openPoliciesChooser() {
		int returnVal = policyChooser.showOpenDialog(SelectionDialog.this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			this.chosenPolicy = policyChooser.getSelectedFile();
			this.policyPathField.setText(this.chosenPolicy.getAbsolutePath());
			if (this.chosenSpreadsheet == null) {
				try {
					DynamicPolicy policy = SifMarshaller
							.unmarshal(this.chosenPolicy);
					if (policy.getSpreadsheetFileName() != null
							&& policy.getSpreadsheetFileName() != "") {
						File sFile = new File(policy.getSpreadsheetFileName());
						if (!sFile.exists()) {
							sFile = new File(chosenPolicy.getParent() + File.separator + policy.getSpreadsheetFileName());
						}
						if (sFile.exists()) {
							this.chosenSpreadsheet = sFile; 
							this.spreadsheetPathField.setText(sFile.getAbsolutePath());
						}
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(
							this,
							"An error has occured during unmarshalling: "
									+ e.getMessage());
				}

			}
		}
	}

	private void openSheetsChooser() {
		int returnVal = sheetChooser.showOpenDialog(SelectionDialog.this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			chosenSpreadsheet = sheetChooser.getSelectedFile();

			FrontDesk frontDesk = FrontDesk.getInstance();

			try {
				InspectionRequest request = frontDesk.requestNewInspection("",
						chosenSpreadsheet);
				frontDesk.scan();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(
						SelectionDialog.this,
						chosenSpreadsheet.getName()
								+ "could not be inspected. Details: "
								+ e.getMessage());
			}
		} else {
			// log.append("Open command cancelled by user." + newline);
		}
	}

	/**
	 * FileFilter for Spreadsheets
	 * 
	 * @author Manuel Lemcke
	 * 
	 */
	public class ExtensionFilter extends FileFilter {
		private String extensions[];

		private String description;

		public ExtensionFilter(String description, String extension) {
			this(description, new String[] { extension });
		}

		public ExtensionFilter(String description, String extensions[]) {
			this.description = description;
			this.extensions = (String[]) extensions.clone();
		}

		public boolean accept(File file) {
			if (file.isDirectory()) {
				return true;
			}
			int count = extensions.length;
			String path = file.getAbsolutePath();
			for (int i = 0; i < count; i++) {
				String ext = extensions[i];
				if (path.endsWith(ext)
						&& (path.charAt(path.length() - ext.length()) == '.')) {
					return true;
				}
			}
			return false;
		}

		public String getDescription() {
			return (description == null ? extensions[0] : description);
		}
	}

}
