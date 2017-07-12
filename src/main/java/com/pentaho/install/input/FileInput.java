package com.pentaho.install.input;

import java.io.File;

public class FileInput extends StringInput {
	public FileInput(String prompt) {
		super(prompt);
	}
	
	public String validate() {
		String msg = super.validate();
		if (msg != null) return msg;
		
		try {
			File f = new File((String)value);
			if (!f.isFile()) {
				msg = String.format("Input [%s] is not a file.", value);
			} else if (!f.exists()) {
				msg = String.format("File [%s] does not exist.", value);
			} else if (!f.canRead()) {
				msg = "No read permission";
			}
		} catch (Exception ex) {
		}
		return msg;
	}
}
