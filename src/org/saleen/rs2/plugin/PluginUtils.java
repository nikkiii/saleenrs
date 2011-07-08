package org.saleen.rs2.plugin;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.saleen.util.Misc;

public class PluginUtils {

	
	public static void addToClasspath(URL... urls) {
		try {
			URLClassLoader loader = (URLClassLoader) Misc.class.getClassLoader();
			Method method = URLClassLoader.class.getDeclaredMethod("addURL",
					new Class<?>[] {URL.class});
			method.setAccessible(true);
			for(URL url : urls)
				method.invoke(loader, new Object[] {url});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
