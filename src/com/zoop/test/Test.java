package com.zoop.test;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Test {

	public static void main1(String[]args) {
		String dir = System.getProperty("user.dir")+"\\src";
		System.out.println(dir);
		File file = new File(dir);
		List<File> list = new ArrayList<File>();
		List<String> cl = new ArrayList<String>();
		loop(file,list);
		Map<String, Object> map = new  HashMap<String, Object>();
		for(File ff : list) {
			String name = ff.getPath();
			name = name.replace(dir+"\\", "");
			name = name.replace(".java", "");
			name = name.replaceAll("\\\\", ".");
			cl.add(name);
			try {
				Class<?> clazz = Class.forName(name);
				Annotation[] annotation = clazz.getAnnotations();
				System.out.println(name+"-----------");
				for(Annotation an : annotation) {
					System.out.println(an.toString());
					if(an.toString().equals("@com.zoop.annotation.Controller()") 
							|| an.toString().equals("@com.zoop.annotation.Service()")){
						map.put(name, clazz.newInstance());
					}
				}
			} catch (Exception e) {
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
	
	public static void main(String[]args){
		try {
			Class<?> clazz = Class.forName("com.zoop.test.TestController");
			Object obj = clazz.newInstance();
			Field[] fields = obj.getClass().getDeclaredFields();
			for(Field field : fields){
				System.out.println(field);
				for(Annotation an : field.getAnnotations()){
					if(an.toString().equals("@com.zoop.annotation.Autowired()")){
						field.setAccessible(true);
						field.set(obj, Class.forName("com.zoop.test.TestService").newInstance());
					}
				}
			}
			if(obj instanceof TestController){
				TestController con = (TestController)obj;
				con.exec();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
