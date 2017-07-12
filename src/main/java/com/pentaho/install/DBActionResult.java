package com.pentaho.install;

import java.util.Map;

public class DBActionResult extends ActionResult {
	//true: manual create db
	private boolean manual;
	
	public DBActionResult(Map<String, DBInstance> dbInstanceMap, boolean manual) {
		super(dbInstanceMap);
		this.manual = manual;
	}

	public boolean isManual() {
		return manual;
	}

	public void setManual(boolean manual) {
		this.manual = manual;
	}
}
