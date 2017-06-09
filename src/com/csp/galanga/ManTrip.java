package com.csp.galanga;

import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.text.Document;

import com.csp.galanga.cmd.Command;
import com.csp.galanga.cmd.CommandFactory;
import com.csp.galanga.snitch.DocumentPrintStream;

public class ManTrip {

	private JFrame frame;
	final JEditorPane editorPane = new JEditorPane();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ManTrip window = new ManTrip();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ManTrip() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		Document doc = editorPane.getDocument();
		DocumentPrintStream documentPrintStream = new DocumentPrintStream(doc, System.out);	    
		System.setOut(documentPrintStream );
		System.setErr(documentPrintStream);
		
		frame = new JFrame("Mine Entrance");
		frame.setBounds(100, 100, 745, 468);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblMining = new JLabel("Mining");
		lblMining.setBounds(10, 11, 46, 14);
		frame.getContentPane().add(lblMining);

		JLabel lblProcessing = new JLabel("Processing");
		lblProcessing.setBounds(10, 73, 77, 14);
		frame.getContentPane().add(lblProcessing);

		String[] processingCmd = {
				Command.SHOW_ACCESS_REPORT,
				Command.CSP_SYS_MIGRATION,
				Command.SHOW_ORG_CHART, 
				Command.SHOW_RUNNING_PROCESSES,
				Command.FIND_PIS_COFINS,
				Command.MINI_PROJECT};
		final JComboBox<String> processingComboBox = new JComboBox<String>(processingCmd);
		processingComboBox.setBounds(10, 98, 315, 23);
		frame.getContentPane().add(processingComboBox);
		
		JButton btnReduce = new JButton("Reduce");
		btnReduce.setBounds(335, 98, 89, 23);
		btnReduce.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				String cmd = (String) processingComboBox.getSelectedItem();
				CommandFactory.executeCommand(cmd);
			}
		});
		frame.getContentPane().add(btnReduce);

		String[] serverCmd = {
				Command.DB_WALKER,
				Command.SELECT_ACCESS_REPORT,
				Command.MIGRATE, 
				Command.INSERT_RULES, 
				Command.SELECT_ORG_CHART, 
				Command.PRE_ACCOUNTING_SQL, 
				Command.UPGRADE_ZPE_DATA,
				Command.SAVE_SOFTWAY,
				Command.READ_EXCEL
		};
		final JComboBox<String> miningComboBox = new JComboBox<String>(serverCmd);
		miningComboBox.setBounds(10, 36, 315, 26);
		frame.getContentPane().add(miningComboBox);

		JButton btnDigIt = new JButton("DIG");
		btnDigIt.setBounds(335, 38, 89, 23);
		btnDigIt.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				String cmd = (String) miningComboBox.getSelectedItem();
				CommandFactory.executeCommand(cmd);				
			}
		});
		frame.getContentPane().add(btnDigIt);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 131, 719, 288);
		frame.getContentPane().add(scrollPane);
		
		scrollPane.setViewportView(editorPane);
		
	}
}
