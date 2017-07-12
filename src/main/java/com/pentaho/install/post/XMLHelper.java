package com.pentaho.install.post;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class XMLHelper {
	protected Element createParamElement(Document doc, String[][] params) {
		Element user = doc.createElement("param");
		for (String[] p : params) {
			user.setAttribute(p[0], p[1]);	
		}
		return user;
	}
	
	protected void addAttribute(Element e, String[][] attributes) {
		for (String[] attribute : attributes) {
			e.setAttribute(attribute[0], attribute[1]);
		}
	}
}
