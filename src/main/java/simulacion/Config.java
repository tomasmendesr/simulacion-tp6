package simulacion;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {

	private static Config instance = null;
	private Properties prop;

	public static Config getInstance(){
		if(instance == null)
			instance = new Config();
		return instance;
	}
	
	public Config(){
		prop = new Properties();
		InputStream input = null;
		
		try {
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			input = classloader.getResourceAsStream("config.properties");
			prop.load(input);
	
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	  }
		
	public String getProperty(String property){
		return prop.getProperty(property);
	}

}