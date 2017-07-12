package com.pentaho.install.input;

import java.io.File;

public class DirInput extends StringInput {
	public DirInput(String prompt) {
		super(prompt);
	}
	
	public String validate() {
		String msg = super.validate();
		if (msg != null) return msg;
		
		try {
			File f = new File((String)value);
			if (!f.isDirectory()) {
				msg = String.format("Input [%s] is not a directory.", value);
			} else if (!f.exists()) {
				msg = String.format("Directory [%s] does not exist.", value);
			} else if (!f.canRead()) {
				msg = "No read permission";
			} else if (!f.canWrite()) {
				msg = "No write permission";
			}
		} catch (Exception ex) {
		}
		return msg;
	}
}
