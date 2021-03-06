package net.anthavio.disquo.browser;

import net.anthavio.sewer.jetty.JettyWrapper;

/**
 * Main class in standalone assembly. Don't work in IDE due to invalid path to jetty root directory
 * 
 * @author martin.vanek
 * 
 */
public class DisqusBrowserJettyMain {

	public static void main(String[] args) {
		try {
			new JettyWrapper().start();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
}
