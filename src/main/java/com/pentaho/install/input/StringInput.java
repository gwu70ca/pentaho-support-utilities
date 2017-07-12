package com.pentaho.install.input;

public class StringInput extends BaseInput {
	protected int length = -1;
	
	public StringInput(String prompt) {
		super.prompt = prompt;
		message = "Invalid input";
	}
	
	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public String validate() {
		String msg = message;
		try {
			String str = (String)value;
			
			if (str == null || str.isEmpty()) {
				return "Input can not be blank";
			} else if (length > 0 && str.length() > length) {
				return "Exceeded maximum length [" + length + "]";
			}
			msg = null;
		} catch (Exception ex) {
		}
		return msg;
	}
}
