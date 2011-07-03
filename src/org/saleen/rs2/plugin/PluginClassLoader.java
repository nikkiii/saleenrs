package org.saleen.rs2.plugin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * A ClassLoader which lets us load plugins from a directory, and access their
 * resources using getResourceAsStream
 * 
 * @author Nikki
 */
public class PluginClassLoader extends ClassLoader {

	/**
	 * The base file
	 */
	private File base;

	public PluginClassLoader(File base) {
		this.base = base;
	}

	@Override
	public Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		Class<?> loadedClass = findLoadedClass(name);

		if (loadedClass == null) {
			try {
				InputStream in = getResourceAsStream(name.replace('.', '/')
						+ ".class");
				try {
					byte[] buffer = new byte[4096];
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					while (true) {
						int read = in.read(buffer, 0, buffer.length);
						if (read == -1) {
							break;
						}
						out.write(buffer, 0, read);
					}
					byte[] bytes = out.toByteArray();
					loadedClass = defineClass(name, bytes, 0, bytes.length);
					if (resolve) {
						resolveClass(loadedClass);
					}
				} finally {
					in.close();
				}
			} catch (Exception e) {
				loadedClass = PluginClassLoader.class.getClassLoader()
						.loadClass(name);
			}
		}
		return loadedClass;
	}

	@Override
	public URL getResource(String name) {
		try {
			return new File(base, name).toURI().toURL();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		try {
			return new FileInputStream(new File(base, name));
		} catch (final IOException e) {
			return null;
		}
	}
}
