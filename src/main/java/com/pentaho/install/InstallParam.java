package com.pentaho.install;

import java.util.Map;

public class InstallParam {
	public String installDir = null;
	public DBParam.DB dbType;
	public PentahoServerParam.SERVER pentahoServerType;
	public AppServerParam.SERVER appServerType;
	public Map<String, DBInstance> dbInstanceMap;
	public boolean manualCreateDb = true;
	public String appServerDir = null;
}
