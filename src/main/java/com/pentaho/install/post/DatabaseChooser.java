package com.pentaho.install.post;

import com.pentaho.install.ActionResult;
import com.pentaho.install.DBParam.DB;
import com.pentaho.install.InstallAction;
import com.pentaho.install.InstallUtil;
import com.pentaho.install.input.SelectInput;

import java.util.Scanner;

/**
 * Select database type
 * 
 * @author gwu
 *
 */
public class DatabaseChooser extends InstallAction {
	private Scanner scanner;
	public DatabaseChooser(Scanner scanner) {
		this.scanner = scanner;
	}
	
	private String prompt() {
		StringBuffer buf = new StringBuffer();
		buf.append(NEW_LINE).append(InstallUtil.bar()).append(NEW_LINE);
		
		int index = 1;
		for (DB db : DB.values()) {
			buf.append(index++).append(": ").append(db).append(NEW_LINE);
		}
		
		buf.append(InstallUtil.bar()).append(NEW_LINE);
		buf.append("Select the database type: ");
		return buf.toString();
	}
	
	public ActionResult execute() {
		SelectInput input = new SelectInput(prompt(), new String[]{"1","2","3","4"});
		
		InstallUtil.ask(scanner, input);
		
		return new ActionResult(DB.values()[Integer.parseInt(input.getValue())-1]);
	}
}
