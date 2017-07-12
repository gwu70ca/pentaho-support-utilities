package com.pentaho.install.post;

import java.util.Scanner;

import com.pentaho.install.ActionResult;
import com.pentaho.install.InstallAction;
import com.pentaho.install.InstallUtil;
import com.pentaho.install.PentahoServerParam;
import com.pentaho.install.input.SelectInput;

public class ServerChooser extends InstallAction {
	private Scanner scanner;
	public ServerChooser(Scanner scanner) {
		this.scanner = scanner;
	}
	
	private String prompt() {
		StringBuffer buf = new StringBuffer();
		buf.append(NEW_LINE).append(bar()).append(NEW_LINE);
		
		buf.append("1: Business Analytics Server\n" );
		buf.append("2: Data Intergration Server\n" );
		
		buf.append(bar()).append(NEW_LINE);
		buf.append("Select the application server type: ");
		return buf.toString();
	}
	
	public ActionResult execute() {
		SelectInput input = new SelectInput(prompt(), new String[]{"1","2"});
		
		InstallUtil.ask(scanner, input);
		
		return new ActionResult(PentahoServerParam.SERVER.values()[Integer.parseInt(input.getValue())-1]);
	}
}
