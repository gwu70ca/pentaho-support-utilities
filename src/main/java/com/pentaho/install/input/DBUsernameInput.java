package com.pentaho.install.input;

import java.util.regex.Pattern;

import com.pentaho.install.DBParam;
import com.pentaho.install.DBParam.DB;

public class DBUsernameInput extends StringInput {
	Pattern pattern = Pattern.compile("[_a-zA-Z][a-zA-Z0-9_]*");
	
	public DBUsernameInput(String prompt, DB dbType) {
		super(prompt);
		length = DBParam.DB_USERNAME_LENGTH.get(dbType);
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
