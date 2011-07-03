package org.saleen.util;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;

public class Misc {

	public static final OperatingSystem CURRENTOS = getOperatingSystem();

	/**
	 * Use the runtime amount, and try to access memory size using reflection...
	 * 
	 * @return Either the total ram or runtime memory
	 */
	public static long getTotalMemory() {
		long memory = Runtime.getRuntime().totalMemory();
		try {
			OperatingSystemMXBean bean = ManagementFactory
					.getOperatingSystemMXBean();
			Method method = bean.getClass().getMethod(
					"getTotalPhysicalMemorySize", new Class<?>[0]);
			method.setAccessible(true);
			memory = (Long) method.invoke(bean, new Object[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return memory;
	}

	public static boolean isWindows() {
		return CURRENTOS == OperatingSystem.WINDOWS;
	}

	public static boolean isUnix() {
		return CURRENTOS == OperatingSystem.LINUX
				|| CURRENTOS == OperatingSystem.SOLARIS;
	}

	public static boolean isMacOS() {
		return CURRENTOS == OperatingSystem.MACOS;
	}

	public static OperatingSystem getOperatingSystem() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("win"))
			return OperatingSystem.WINDOWS;
		if (osName.contains("mac"))
			return OperatingSystem.MACOS;
		if (osName.contains("SOLARIS"))
			return OperatingSystem.SOLARIS;
		if (osName.contains("sunos"))
			return OperatingSystem.SOLARIS;
		if (osName.contains("LINUX"))
			return OperatingSystem.LINUX;
		if (osName.contains("unix"))
			return OperatingSystem.LINUX;
		return OperatingSystem.UNKNOWN;
	}

	public enum OperatingSystem {
		LINUX, SOLARIS, WINDOWS, MACOS, UNKNOWN
	}
}
