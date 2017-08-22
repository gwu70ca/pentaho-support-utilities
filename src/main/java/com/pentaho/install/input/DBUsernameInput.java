package com.pentaho.install.input;

import com.pentaho.install.db.Dialect;

import java.util.regex.Pattern;

public class DBUsernameInput extends StringInput {
	Pattern pattern = Pattern.compile("[_a-zA-Z][a-zA-Z0-9_]*");
	
	public DBUsernameInput(String prompt, Dialect dialect) {
		super(prompt);
		length = dialect.getDbUserNameLength();
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
