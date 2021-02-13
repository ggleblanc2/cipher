package com.ggl.testing;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CipherGUI implements Runnable {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new CipherGUI());
	}
	
	private final CipherModel model;
	
	private CipherPanel encryptPanel;
	private CipherPanel decryptPanel;
	
	public CipherGUI() {
		this.model = new CipherModel();
	}

	@Override
	public void run() {
		JFrame frame = new JFrame("Cipher GUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.add(createMainPanel(), BorderLayout.CENTER);
		
		frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);

		System.out.println(frame.getSize());
	}
	
	private JTabbedPane createMainPanel() {
		final JTabbedPane tabbedPane = new JTabbedPane();
		Font font = tabbedPane.getFont().deriveFont(16f);
		tabbedPane.setFont(font);
		
		String plainTextTitle = "Plain Text";
		String encryptedTitle = "Cipher Text";
		encryptPanel = new CipherPanel(this, model, model.getEncryptTitle(), 
				plainTextTitle, encryptedTitle);
		tabbedPane.add("Encode Text", encryptPanel.getPanel());
		decryptPanel = new CipherPanel(this, model, model.getDecryptTitle(),
				encryptedTitle, plainTextTitle);
		tabbedPane.add("Decode Text", decryptPanel.getPanel());
		
		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
	        public void stateChanged(ChangeEvent event) {
	           if (tabbedPane.getSelectedIndex() == 0) {
	        	   encryptPanel.setKeyPhrase(model.getKeyPhrase());
	        	   encryptPanel.setInputText(model.getDecryptedText());
	           }
	           
	           if (tabbedPane.getSelectedIndex() == 1) {
	        	   decryptPanel.setKeyPhrase(model.getKeyPhrase());
	        	   decryptPanel.setInputText(model.getEncryptedText());
	           }
	        }
	    });
		
		return tabbedPane;
	}
	
	public CipherPanel getEncryptPanel() {
		return encryptPanel;
	}

	public CipherPanel getDecryptPanel() {
		return decryptPanel;
	}

	public class CipherPanel {
		
		private final CipherGUI frame;
		
		private final CipherModel model;
		
		private final JPanel panel;
		
		private JTextArea inputArea;
		private JTextArea outputArea;
		
		private JTextField keyPhraseField;
		
		private final String buttonText;
		private final String inputTitle;
		private final String outputTitle;

		public CipherPanel(CipherGUI frame, CipherModel model, 
				String buttonText, String inputTitle, String outputTitle) {
			this.frame = frame;
			this.model = model;
			this.buttonText = buttonText;
			this.inputTitle = inputTitle;
			this.outputTitle = outputTitle;
			this.panel = createMainPanel();
		}
		
		private JPanel createMainPanel() {
			JPanel panel = new JPanel(new GridBagLayout());
			panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			Font font = panel.getFont().deriveFont(16f);
			
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.LINE_START;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.gridx = 0;
			gbc.gridy = 0;
			
			JLabel keyPhraseLabel = new JLabel("Key Phrase");
			keyPhraseLabel.setFont(font);
			panel.add(keyPhraseLabel, gbc);
			
			gbc.gridy++;
			keyPhraseField = new JTextField(50);
			keyPhraseField.setFont(font);
			panel.add(keyPhraseField, gbc);
			
			gbc.gridy++;
			JLabel inputLabel = new JLabel(inputTitle);
			inputLabel.setFont(font);
			panel.add(inputLabel, gbc);
			
			gbc.gridy++;
			inputArea = new JTextArea(10, 50);
			inputArea.setFont(font);
			inputArea.setLineWrap(true);
			inputArea.setWrapStyleWord(true);
			JScrollPane scrollPane = new JScrollPane(inputArea);
			panel.add(scrollPane, gbc);
			
			gbc.gridy++;
			JButton button = new JButton(buttonText);
			button.addActionListener(new ButtonListener(frame, model));
			button.setFont(font);
			panel.add(button, gbc);
			
			gbc.gridy++;
			JLabel outputLabel = new JLabel(outputTitle);
			outputLabel.setFont(font);
			panel.add(outputLabel, gbc);
			
			gbc.gridy++;
			outputArea = new JTextArea(10, 50);
			outputArea.setEditable(false);
			outputArea.setFont(font);
			outputArea.setLineWrap(true);
			outputArea.setWrapStyleWord(true);
			scrollPane = new JScrollPane(outputArea);
			panel.add(scrollPane, gbc);
			
			return panel;
		}

		public JPanel getPanel() {
			return panel;
		}
		
		public void setKeyPhrase(String keyPhrase) {
			this.keyPhraseField.setText(keyPhrase);
		}
		
		public String getKeyPhrase() {
			return keyPhraseField.getText().trim();
		}
		
		public void setInputText(String inputText) {
			this.inputArea.setText(inputText);
		}
		
		public String getInputText() {
			return inputArea.getText().trim();
		}
		
		public void setOutputText(String text) {
			this.outputArea.setText(text);
		}
		
	}
	
	public class ButtonListener implements ActionListener {
		
		private final CipherGUI frame;
		
		private final CipherModel model;

		public ButtonListener(CipherGUI frame, CipherModel model) {
			this.frame = frame;
			this.model = model;
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			JButton button = (JButton) event.getSource();
			String buttonText = button.getText();
			
			if (buttonText.equals(model.getDecryptTitle())) {
				String keyPhrase = frame.getDecryptPanel().getKeyPhrase();
				String inputText = frame.getDecryptPanel().getInputText();
				if (keyPhrase.isEmpty() || inputText.isEmpty()) {
					return;
				}
				
				model.setKeyPhrase(keyPhrase);
				model.setEncryptedText(inputText);
				model.decryptText();
				frame.getDecryptPanel().setOutputText(model.getDecryptedText());
			}
			
			if (buttonText.equals(model.getEncryptTitle())) {
				String keyPhrase = frame.getEncryptPanel().getKeyPhrase();
				String inputText = frame.getEncryptPanel().getInputText();
				if (keyPhrase.isEmpty() || inputText.isEmpty()) {
					return;
				}
				
				model.setKeyPhrase(keyPhrase);
				model.setDecryptedText(inputText);
				model.encryptText();
				frame.getEncryptPanel().setOutputText(model.getEncryptedText());
			}
		}
		
	}
	
	public class CipherModel {
		
		private final String encryptTitle;
		private final String decryptTitle;
		
		private String keyPhrase;
		private String decryptedText;
		private String encryptedText;
		
		public CipherModel() {
			this.encryptTitle = "Encrypt Text";
			this.decryptTitle = "Decrypt Text";		
		}
		
		public String getKeyPhrase() {
			return keyPhrase;
		}
		
		public void setKeyPhrase(String keyPhrase) {
			this.keyPhrase = keyPhrase;
		}
		
		public String getDecryptedText() {
			return decryptedText;
		}
		
		public void setDecryptedText(String decryptedText) {
			this.decryptedText = decryptedText;
		}
		
		public String getEncryptedText() {
			return encryptedText;
		}
		
		public void setEncryptedText(String encryptedText) {
			this.encryptedText = encryptedText;
		}
		
		public void decryptText() {
			StringBuilder builder = new StringBuilder();
			int keyIndex = 0;
			int textIndex = 0;
			
			// ASCII characters go from 32 to 126
			while (textIndex < encryptedText.length()) {
				int shift = (int) keyPhrase.charAt(keyIndex++) - 31;
				char c = encryptedText.charAt(textIndex++);
				int value = ((int) c - shift);
				value = (value < 32) ? value + 95 : value;
				char d = (char) value;
				builder.append(d);
				keyIndex %= keyPhrase.length();
			}
			
			decryptedText = builder.toString();
		}
		
		public void encryptText() {
			StringBuilder builder = new StringBuilder();
			int keyIndex = 0;
			int textIndex = 0;
			
			// ASCII characters go from 32 to 126
			while (textIndex < decryptedText.length()) {
				int shift = (int) keyPhrase.charAt(keyIndex++) - 31;
				char c = decryptedText.charAt(textIndex++);
				int value = (int) c + shift;
				value = (value > 126) ? value - 95 : value;
				char d = (char) value;
				builder.append(d);
				keyIndex %= keyPhrase.length();
			}
			
			encryptedText = builder.toString();
		}

		public String getEncryptTitle() {
			return encryptTitle;
		}

		public String getDecryptTitle() {
			return decryptTitle;
		}
		
	}

}
