#!/bin/sh
java -cp .:lib/* com.pentaho.support.connection.JDBCConnector $@
