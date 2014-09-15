/*
 * Copyright (C) 2014 Michele Balistreri
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package com.briksoftware.updatefx.util;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import sun.management.VMManagement;

public final class PIDUtil {
	private PIDUtil() {
	}

	public static int getPID() {
		try {
			return getPIDByRuntimeName();
		} catch (NumberFormatException e) {
			try {
				return getPIDForOracleJVM();
			} catch(Throwable t) {
				throw new UnsupportedOperationException("this JVM does not support PID retrieval");
			}
		}
	}

	protected static int getPIDByRuntimeName() throws NumberFormatException {
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		String pidString = runtime.getName().split("@")[0];
		
		return Integer.parseInt(pidString);
	}

	protected static int getPIDForOracleJVM() throws Exception {
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
		Field jvm = runtime.getClass().getDeclaredField("jvm");
		jvm.setAccessible(true);
		VMManagement mgmt = (VMManagement) jvm.get(runtime);
		Method getProcessId = mgmt.getClass().getDeclaredMethod("getProcessId");
		getProcessId.setAccessible(true);

		return (Integer) getProcessId.invoke(mgmt);
	}
}
