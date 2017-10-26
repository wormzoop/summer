package com.zoop.test;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.zoop.annotation.RequestMapping;

public class Test {

	public static void main(String[]args) {
		String dir = System.getProperty("user.dir")+"\\src";
		System.out.println(dir);
		File file = new File(dir);
		List<File> list = new ArrayList<File>();
		List<String> cl = new ArrayList<String>();
		loop(file,list);
		List<Object> controllerList = new ArrayList<Object>();
		List<Object> serviceList = new ArrayList<Object>();
		Map<String, Object> objMap = new HashMap<String, Object>();
		Map<String, Object> reqMethod = new HashMap<String, Object>();
		Map<String, Object> reqObj = new HashMap<String, Object>();
		for(File ff : list) {
			String name = ff.getPath();
			name = name.replace(dir+"\\", "");
			name = name.replace(".java", "");
			name = name.replaceAll("\\\\", ".");
			cl.add(name);
			try {
				Class<?> clazz = Class.forName(name);
				Annotation[] annotation = clazz.getAnnotations();
				for(Annotation an : annotation) {
					if(an.toString().equals("@com.zoop.annotation.Controller()")){
						controllerList.add(clazz.newInstance());
						objMap.put(name,clazz.newInstance());
					}else if(an.toString().equals("@com.zoop.annotation.Service()")){
						serviceList.add(clazz.newInstance());
						objMap.put(name,clazz.newInstance());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for(Object obj : controllerList){
			Field[] fields = obj.getClass().getDeclaredFields();
			for(Field f : fields){
				Annotation[] annotation = f.getAnnotations();
				for(Annotation an : annotation){
					System.out.println(an.toString());
					if(an.toString().equals("@com.zoop.annotation.Autowired()")){
						System.out.println(f.getType().getName());
						f.setAccessible(true);
						try {
							f.set(obj, objMap.get(f.getType().getName()));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			Method[] methods = obj.getClass().getMethods();
			for(Method m : methods){
				Annotation[] annotations = m.getAnnotations();
				for(Annotation an : annotations){
					if(an.annotationType() == RequestMapping.class){
						try {
							String req = m.getAnnotation(RequestMapping.class).value();
							reqMethod.put(req, m);
							reqObj.put(req, obj);
						} catch (Exception e) {
							e.printStackTrace();
						} 
					}
				}
			}
			for(String key : reqMethod.keySet()){
				System.out.println("------"+key);
			}
			for(String key : reqObj.keySet()){
				System.out.println("jijiji"+key);
			}
		}
		try {
			System.out.println(JSON.toJSONString(((Method)reqMethod.get("/list")).invoke(reqObj.get("/list"), null)));
		} catch (Exception e) {
			e.printStackTrace();
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
	
	public static void main1(String[]args){
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
			Method[] methods = clazz.getMethods();
			for(Method m : methods){
				if(m.toString().equals("public java.util.Map com.zoop.test.TestController.exec()")){
					Map<String, Object> map = (Map<String, Object>) m.invoke(obj, null);
					System.out.println(map);
					List<Map<String, Object>> ll = new ArrayList<Map<String, Object>>();
					ll.add(map);
					Map<String, Object> map1 = new HashMap<String, Object>();
					map1.put("id", "jiji");
					map1.put("name", "1111jiji");
					ll.add(map1);
					System.out.println(JSON.toJSONString(ll));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
