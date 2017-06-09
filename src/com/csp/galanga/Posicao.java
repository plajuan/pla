package com.csp.galanga;

import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JComboBox;

public class Posicao {

	public JFrame frame;
	private JTextField textField;
	private Object[] rolesData;
	private Object[] rolesPosicaoData;
	private Object[] allRolesData;
	private JComboBox comboBox;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Posicao window = new Posicao();
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
	public Posicao() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 333);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblPosio = new JLabel("Posi\u00E7\u00E3o");
		lblPosio.setBounds(10, 11, 46, 14);
		frame.getContentPane().add(lblPosio);
		
		comboBox = new JComboBox();
		comboBox.setBounds(55, 8, 214, 20);
		setPosicoes();
		frame.getContentPane().add(comboBox);
		
		textField = new JTextField();
		textField.setBounds(55, 8, 214, 20);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		textField.setVisible(false);
		
		JList rolesPosicao = new JList();		
		JList roles = new JList();
		
		JScrollPane leftPane = new JScrollPane();
		leftPane.setBounds(10, 46, 154, 205);
		frame.getContentPane().add(leftPane);
		leftPane.setViewportView(roles);
		
		JButton btnToRight = new JButton(">>");
		btnToRight.setBounds(174, 74, 66, 23);
		frame.getContentPane().add(btnToRight);
		
		JButton btnToLeft = new JButton("<<");
		btnToLeft.setBounds(174, 108, 66, 23);
		frame.getContentPane().add(btnToLeft);
		
		JScrollPane rightPane = new JScrollPane();
		rightPane.setBounds(247, 46, 154, 205);
		frame.getContentPane().add(rightPane);
		rightPane.setViewportView(rolesPosicao);
		
		JButton btnNovo = new JButton("Novo");
		btnNovo.setBounds(10, 261, 95, 23);
		frame.getContentPane().add(btnNovo);
		
		JButton btnSalvar = new JButton("Salvar");
		btnSalvar.setBounds(105, 261, 95, 23);
		frame.getContentPane().add(btnSalvar);
	}

	private void setPosicoes() {
		comboBox.addItem("opção 1");
		comboBox.addItem("opção 2");
	}
}
