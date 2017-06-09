package com.csp.galanga.snitch;

import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class DocumentPrintStream extends PrintStream {

	private Document document;

	public DocumentPrintStream(OutputStream out) {
		super(out);

	}

	public DocumentPrintStream(Document document, OutputStream delegateStream) {
		super(delegateStream);
		this.document = document;
	}

	@Override
	public void print(String s) {
		int offset = document.getLength();
		try {
			document.insertString(offset, s+"\n", null);
		} catch (BadLocationException e) {
		
		}
		super.print(s); // write to the delegate stream
	}

}
