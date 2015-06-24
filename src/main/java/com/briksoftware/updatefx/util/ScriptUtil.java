package com.briksoftware.updatefx.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public final class ScriptUtil {
	private ScriptUtil() { }
	
	public static Path copyScript(String name) throws Exception {
		Path script = Paths.get(System.getProperty("java.io.tmpdir"), name);
		
		try(OutputStream fos = Files.newOutputStream(script, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE); 
				InputStream is = ScriptUtil.class.getResourceAsStream(name)) {
			byte[] buffer = new byte[4096];
			int n;
			
			while ((n = is.read(buffer)) != -1) {
				fos.write(buffer, 0, n);
			}
		}
		
		return script;
	}
}
