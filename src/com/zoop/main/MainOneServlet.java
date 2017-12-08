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
import com.zoop.annotation.Autowired;
import com.zoop.annotation.Controller;
import com.zoop.annotation.RequestMapping;

public class MainOneServlet extends HttpServlet{
	
	private static final long serialVersionUID = -3793970781170651037L;

	private static final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";
	
	//项目路径classes下面
	private static String classes;
	
	//实例化的对象集合,名字全路径对应
	private static Map<String,Object> objectMap = new HashMap<String,Object>();
	
	//uri对应的对象
	private static Map<String,Object> uriObject = new HashMap<String,Object>();
	
	//uri对应的方法
	private static Map<String,Method> uriMethod = new HashMap<String,Method>();
	
	//处理请求
	public void service(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException{
		String uri = request.getRequestURI();
		String reqUri = uri.substring(uri.indexOf("/", 2));
		try {
			Method m = (Method)uriMethod.get(reqUri);
			System.out.println(m.getName());
			Object cl = uriObject.get(reqUri);
			System.out.println(cl.toString());
			String json = JSON.toJSONString(((Method)uriMethod.get(reqUri)).invoke(uriObject.get(reqUri)));
			PrintWriter out = response.getWriter();
			out.write(json);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始化
	 * 读取配置文件,找到需要扫描的包
	 * 实例化对象
	 */
	public void init(){
		String dir = this.getServletConfig().getServletContext().getRealPath("/");
		System.out.println("项目路径::::::::::::::::"+dir);
		String context = this.getServletConfig().getInitParameter(CONTEXT_CONFIG_LOCATION);
		classes = dir+"WEB-INF"+File.separator+"classes";
		String contextPath = classes+File.separator+context;
		System.out.println("配置文件路径:::::::::::::"+contextPath);
		File file = new File(contextPath);
		//所有需要扫描的类
		List<String> javaList = new ArrayList<String>();
		if(file.exists()) {
			List<String> pacs = XmlHandle.annotationPac(file);
			System.out.println("需要扫描的包:::::::::"+pacs);
			for(String str : pacs) {
				File f = new File(classes+File.separator+str);
				System.out.println(f.listFiles());
				loop(f,javaList);
			}
			System.out.println("所有扫描的类:::::::::::::::::::");
			for(String str : javaList) {
				System.out.println(str);
			}
			System.out.println(":::::::::::::::::::::::::::");
			instanceAllObject(javaList);
			for(String key : objectMap.keySet()) {
				Class clazz = objectMap.get(key).getClass();
				Annotation[] annotations = clazz.getAnnotations();
				for(Annotation annotation : annotations) {
					//处理controller类
					if(annotation.annotationType() == Controller.class) {
						//注入service
						Field[] fields = clazz.getDeclaredFields();
						for(Field field : fields) {
							Annotation[] ans = field.getAnnotations();
							for(Annotation an : ans) {
								if(an.annotationType() == Autowired.class) {
									field.setAccessible(true);
									try {
										field.set(objectMap.get(key), objectMap.get(field.getType().getName()));
									} catch (IllegalArgumentException e) {
										e.printStackTrace();
									} catch (IllegalAccessException e) {
										e.printStackTrace();
									}
								}
							}
						}
						Method[] methods = clazz.getMethods();
						for(Method method : methods) {
							Annotation[] ans = method.getAnnotations();
							for(Annotation an : ans) {
								if(an.annotationType() == RequestMapping.class) {
									String value = method.getAnnotation(RequestMapping.class).value();
									uriObject.put(value, objectMap.get(key));
									uriMethod.put(value, method);
								}
							}
						}
					}
				}
			}
		}else {
			System.out.println(contextPath+" 配置文件不存在");
		}
	}
	
	//实例化所有的类,名字全称对应对象
	public void instanceAllObject(List<String> list){
		for(String str : list) {
			try {
				Class<?> clazz = Class.forName(str);
				objectMap.put(str, clazz.newInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	//循环获得包下的类
	public void loop(File file, List<String> list) {
		if(file.isDirectory()) {
			File[] fileArr = file.listFiles();
			for(File f : fileArr) {
				loop(f,list);
			}
		}else {
			String filePath = file.getPath();
			filePath = filePath.replace(classes+File.separator, "");
			filePath = filePath.replaceAll("\\\\", ".");
			filePath = filePath.replace(".class", "");
			list.add(filePath);
		}
	}
	
}
