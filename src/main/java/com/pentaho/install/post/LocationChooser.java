package com.pentaho.install.post;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import com.pentaho.install.ActionResult;
import com.pentaho.install.InstallAction;
import com.pentaho.install.InstallUtil;
import com.pentaho.install.PentahoServerParam.SERVER;
import com.pentaho.install.action.MoveFileAction;
import com.pentaho.install.input.BooleanInput;
import com.pentaho.install.input.DirInput;

/**
 * Ask for where the files were placed
 * 
 * @author gwu
 *
 */
public class LocationChooser extends InstallAction {
	Scanner scanner;
	
	private SERVER serverType;
	
	public LocationChooser(Scanner scanner) {
		this.scanner = scanner;
	}
	
	public void setServerType(SERVER serverType) {
		this.serverType = serverType;
	}

	public ActionResult execute() {
		DirInput input = new DirInput("Input the location where Pentaho software will be installed: ");
		InstallUtil.ask(scanner, input);
		String dir = input.getValue();
		
		String sourceDir = "biserver-ee";
		String targetDir = "server/biserver-ee";
		
		//if (serverType.equals(SERVER.DI)) {
		//	sourceDir = "pdi-ee";
		//	targetDir = "server/data-integration-server";	
		//}
		
		if (serverType.equals(SERVER.BA)) {
			File source = new File(dir, sourceDir);
			File target = new File(dir, targetDir);
			
			if (source.exists() && !target.exists()) {
				BooleanInput moveServerDirInput = new BooleanInput("Installer is going to move the files under directory [" + source.getAbsolutePath() + "] to [" + target.getAbsolutePath() + "]. Do you want to continue (y/n)? ");
				InstallUtil.ask(scanner, moveServerDirInput);
				if (moveServerDirInput.yes()) {
					if (target.mkdirs()) {
						InstallUtil.output("Moving " + source.getAbsolutePath() + " to " + target.getAbsolutePath());
						
						try {
							MoveFileAction action = new MoveFileAction(source, target);
							action.execute();
						} catch (IOException ex) {
							BooleanInput continueInput = new BooleanInput("Installer could not copy the files, do you want to continue? ");
							InstallUtil.ask(scanner, continueInput);
							if (!continueInput.yes()) {
								System.exit(0);
							}
						}
						
						InstallUtil.output("\t[done]");
					} else {
						BooleanInput continueInput = new BooleanInput("Installer could not create directory [" + target.getAbsolutePath() + "]");
						InstallUtil.ask(scanner, continueInput);
						if (!continueInput.yes()) {
							System.exit(0);
						}
					}
				}
			}
		}
		
		return new ActionResult(dir);
	}
}
