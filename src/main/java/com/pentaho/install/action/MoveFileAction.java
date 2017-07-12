package com.pentaho.install.action;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class MoveFileAction {
	private Path source;
	private Path target;
	
	public MoveFileAction(File s, File t) {
		source = s.toPath();
		target = t.toPath();
	}
	
	public Path getSource() {
		return source;
	}

	public void setSource(Path source) {
		this.source = source;
	}

	public Path getTarget() {
		return target;
	}

	public void setTarget(Path target) {
		this.target = target;
	}

	public void execute() throws IOException {
		Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
	}
}
