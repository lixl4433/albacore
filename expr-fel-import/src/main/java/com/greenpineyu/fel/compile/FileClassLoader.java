package com.greenpineyu.fel.compile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileClassLoader extends ClassLoader {
	private static final Logger logger = LoggerFactory.getLogger(FileClassLoader.class);
	private String dir;
	public static final String fileType = ".class";

	public FileClassLoader(ClassLoader arg0, String dir) {
		super(arg0);
		this.dir = dir;
	}

	@Override
	public Class<?> findClass(String name) {
		byte[] data = loadClassData(name);
		return defineClass(name, data, 0, data.length);
	}

	public byte[] loadClassData(String name) {
		name = name.replaceAll("\\.", "/");
		FileInputStream fis = null;
		byte[] data = null;
		try {
			fis = new FileInputStream(new File(dir + name + fileType));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int ch = 0;
			while ((ch = fis.read()) != -1) {
				baos.write(ch);
			}
			data = baos.toByteArray();
		} catch (IOException e) {
			logger.error("", e);
		} finally {
			if (fis != null) try {
				fis.close();
			} catch (IOException e) {
				logger.error("", e);
			}
		}
		return data;
	}
}
