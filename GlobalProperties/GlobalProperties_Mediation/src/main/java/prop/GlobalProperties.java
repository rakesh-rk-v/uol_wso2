package prop;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.apache.synapse.MessageContext; 
import org.apache.synapse.mediators.AbstractMediator;

public class GlobalProperties extends AbstractMediator { 

	public boolean mediate(MessageContext context) { 
		try {
		String folderPath = System.getProperty("carbon.home")+"/CustomDevConfigs";
		log.info(new String("Properties File Folder Path is "+folderPath));
		File dir = new File(folderPath);
		if(dir.isDirectory()) {
			File [] files = dir.listFiles(new PropertiesFileFilter());
			log.debug(new String(files.length+" No.of Files Loading .............."));
			Properties properties = new Properties();
			for(File file: files) {
				FileInputStream fileInput = new FileInputStream(file);
				properties.load(fileInput);
				for(String propertyName : properties.stringPropertyNames()) {
					System.setProperty(propertyName, properties.getProperty(propertyName));
				}

			}
		}else {
			log.error(new String("Given Folder Path:"+dir+" is not a Directory."));
			return false;
		}
		
		}
		catch(Exception e) {
			log.error(e);
			return false;
		}
		return true;
	}
}
