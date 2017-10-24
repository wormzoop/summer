package com.zoop.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Test {

	public static void main(String[]args) {
		String dir = System.getProperty("user.dir")+"\\src";
		System.out.println(dir);
		File file = new File(dir);
		List<File> list = new ArrayList<File>();
		loop(file,list);
		for(File ff : list) {
			System.out.println(ff.getName());
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
