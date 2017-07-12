package com.pentaho.install.input;

public class BooleanInput extends BaseInput {
	public BooleanInput(String prompt) {
		super.prompt = prompt;
		message = "Input value must be either 'y' or 'n' (case insensitive)";
	}
	
	public String validate() {
		String msg = message;
		try {
			String str = (String)value;
			if ("y".equalsIgnoreCase(str) || "n".equalsIgnoreCase(str)) {
				msg = null;
			}
		} catch (Exception ex) {
		}
		return msg;
	}
	
	public boolean yes() {
		String str = (String)value;
		return "y".equalsIgnoreCase(str) || "Y".equalsIgnoreCase(str);
	}
}
