package org.saleen.util;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class ClasspathUtils {
	
	public static void addToClasspath(URL... urls) {
		try {
			URLClassLoader loader = (URLClassLoader) ClasspathUtils.class.getClassLoader();
			Method method = URLClassLoader.class.getDeclaredMethod("addURL",
					new Class<?>[] {URL.class});
			method.setAccessible(true);
			for(URL url : urls)
				method.invoke(loader, new Object[] {url});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean classpathContains(URL url) {
		String mainPath = url.getPath();
		String mainFile = mainPath.substring(mainPath.lastIndexOf("/"));
		URLClassLoader loader = (URLClassLoader) ClasspathUtils.class.getClassLoader();
		for(URL sub : loader.getURLs()) {
			if(sub.equals(url)) {
				return true;
			}
			String path = sub.getPath();
			String file = path.substring(path.lastIndexOf("/"));
			if(path.equals(mainPath) || file.equals(mainFile)) {
				return true;
			}
		}
		return false;
	}
}
