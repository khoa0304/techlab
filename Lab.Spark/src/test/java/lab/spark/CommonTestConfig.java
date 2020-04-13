package lab.spark;

import java.io.File;

public class CommonTestConfig {

	static {
		File file = new File(".");
		String absolutepath = file.getAbsolutePath();
		System.setProperty("hadoop.home.dir",absolutepath
				+ File.separatorChar+ "src" 
				+ File.separatorChar+ "main"
				+ File.separatorChar+ "resources");
	}
}
