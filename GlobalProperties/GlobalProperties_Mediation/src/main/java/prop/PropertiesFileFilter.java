package prop;

import java.io.File;
import java.io.FilenameFilter;

public class PropertiesFileFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		return name.endsWith(".properties");
	}

}
