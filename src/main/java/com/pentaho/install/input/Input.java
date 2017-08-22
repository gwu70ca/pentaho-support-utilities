package com.pentaho.install.input;

public interface Input {
    public default String validate() {
        return "You need a validator";
    }

    public String getPrompt();

    public String getValue();

    public String getDefaultValue();

    public void setDefaultValue(String defaultValue);

    public void setValue(String value);
}
