package com.zoop.test;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

public class Test {

	public static void main(String[]args) {
		String dir = System.getProperty("user.dir")+"\\src";
		System.out.println(dir);
		File file = new File(dir);
		List<File> list = new ArrayList<File>();
		List<String> cl = new ArrayList<String>();
		loop(file,list);
		for(File ff : list) {
			String name = ff.getPath();
			name = name.replace(dir+"\\", "");
			name = name.replace(".java", "");
			name = name.replaceAll("\\\\", ".");
			cl.add(name);
			try {
				Class clazz = Class.forName(name);
				Annotation[] annotation = clazz.getAnnotations();
				System.out.println(name+"-----------");
				for(Annotation an : annotation) {
					System.out.println(an.toString());
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void loop(File file, List<File> list) {
		File[] f = file.listFiles();
		for(File item : f) {
			if(item.isDirectory()) {
				loop(item, list);
			}else {
				String name = item.getName();
				name = name.substring(name.lastIndexOf('.')+1);
				if(name.equals("java")) {
					list.add(item);
				}
			}
		}
	}
	
}
