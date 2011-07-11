package org.saleen.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Utilities to modify the current classpath!
 * @author Nikki
 *
 */
public class ClasspathUtils {
	
	/**
	 * Add URLs to the classpath
	 * @param urls
	 * 			The URLs
	 */
	public static void addToClasspath(URL... urls) {
		try {
			URLClassLoader loader = (URLClassLoader) ClassLoader.getSystemClassLoader();
			Method method = URLClassLoader.class.getDeclaredMethod("addURL",
					new Class<?>[] {URL.class});
			method.setAccessible(true);
			for(URL url : urls)
				method.invoke(loader, new Object[] {url});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Check if the classpath contains a current url
	 * @param url
	 * 			The url
	 * @return
	 * 			True if found
	 */
	public static boolean classpathContains(URL url) {
		String mainPath = url.getPath();
		String mainFile = mainPath.substring(mainPath.lastIndexOf("/"));
		URLClassLoader loader = (URLClassLoader) ClassLoader.getSystemClassLoader();
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
	
	public static File extractToTemp(File file, String filename) throws IOException {
		File outputFile = new File(System.getProperty("java.io.tmpdir"), filename);
		if(outputFile.exists()) {
			return outputFile;
		}
		JarFile jar = new JarFile(file);
		JarEntry entry = jar.getJarEntry(filename);
		InputStream input = jar.getInputStream(entry);
		OutputStream output = new FileOutputStream(outputFile);
		try {
			Streams.copy(input, output);
		} finally {
			input.close();
			output.close();
		}
		return outputFile;
	}
}
