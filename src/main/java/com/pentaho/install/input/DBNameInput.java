package com.pentaho.install.input;

import java.util.regex.Pattern;

public class DBNameInput extends StringInput {
	Pattern pattern = Pattern.compile("[_a-zA-Z][a-zA-Z0-9_]*");
	
	public DBNameInput(String prompt, int length) {
		super(prompt);
		this.length = length;
	}
	
	public String validate() {
		String msg = super.validate();
		if (msg != null) {
			return msg;
		}
		
		boolean valid = pattern.matcher(value).matches();
		if (valid) {
			msg = null;
		}
		
		return msg;
	}
}
