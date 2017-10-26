package com.zoop.main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.sun.xml.internal.ws.wsdl.writer.document.Service;
import com.zoop.annotation.Autowired;
import com.zoop.annotation.Controller;
import com.zoop.annotation.RequestMapping;

//处理web请求找到对应的controller
public class MainServlet extends HttpServlet{

	private static final long serialVersionUID = -6300271163121103199L;

	//请求对应方法
	Map<String, Object> reqMethod = new HashMap<String, Object>();
	//请求对应对象
	Map<String, Object> reqObj = new HashMap<String, Object>();
	
	public void service(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException{
		String uri = request.getRequestURI();
		try {
			String json = JSON.toJSONString(((Method)reqMethod.get(uri)).invoke(reqObj.get(uri), null));
			PrintWriter out = response.getWriter();
			out.write(json);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//实例化注解的类
	public void init(){
		String dir = System.getProperty("user.dir")+"\\src";
		System.out.println(dir);
		File file = new File(dir);
		System.out.println(file.listFiles());
		List<File> list = new ArrayList<File>();
		List<String> cl = new ArrayList<String>();
		loop(file,list);
		//存放有controller注解的对象
		List<Object> controllerList = new ArrayList<Object>();
		//存放有service注解的对象
		List<Object> serviceList = new ArrayList<Object>();
		Map<String, Object> objMap = new HashMap<String, Object>();
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
					if(an.annotationType() == Controller.class){
						controllerList.add(clazz.newInstance());
						objMap.put(name,clazz.newInstance());
					}else if(an.annotationType() == Service.class){
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
					if(an.annotationType() == Autowired.class){
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
		}
	}
	
	//循环获得java文件
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
