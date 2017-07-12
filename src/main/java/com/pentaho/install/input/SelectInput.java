package com.pentaho.install.input;

public class SelectInput extends BaseInput {
	public SelectInput(String prompt, String[] values) {
		setPrompt(prompt);
		setValues(values);
	}
	
	public String validate() {
		for (String s : values) {
			if (s.equals(value)) {
				return null;
			}
		}
		return "Invalid selection";
	}
}
