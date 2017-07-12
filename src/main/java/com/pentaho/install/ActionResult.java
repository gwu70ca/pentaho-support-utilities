package com.pentaho.install;

public class ActionResult {
	private Object returnedValue;

	public ActionResult(Object obj) {
		this.returnedValue = obj;
	}
	
	public Object getReturnedValue() {
		return returnedValue;
	}

	public void setReturnedValue(Object returnedValue) {
		this.returnedValue = returnedValue;
	}
	
	public String toString() {
		return this.returnedValue.toString();
	}
}
