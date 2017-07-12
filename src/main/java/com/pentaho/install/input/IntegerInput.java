package com.pentaho.install.input;

public class IntegerInput extends StringInput {
	public IntegerInput(String prompt) {
		super(prompt);
	}
	
	public String validate() {
		String msg = super.validate();
		if (msg != null) {
			return msg;
		}
		
		try {
			Integer.parseInt(value);
			msg = null;
		} catch (Exception ex) {
			msg = "Invalid port number";
		}
		return msg;
	}
}
