package com.zoop.main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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

public class MainOneServlet extends HttpServlet{
	
	private static final long serialVersionUID = -3793970781170651037L;

	private static final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";
	
	//项目路径classes下面
	private static String classes;
	
	//实例化的对象集合
	private static Map<String,Object> objectMap = new HashMap<String,Object>();
	
	//实例化对象的有注解的方法集合
	private static Map<String,Object> methodMap = new HashMap<String,Object>();
	
	//处理请求
	public void service(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException{
		String uri = request.getRequestURI();
		try {
			String json = JSON.toJSONString(((Method)methodMap.get(uri)).invoke(objectMap.get(uri), null));
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
		String classes = dir+File.separator+"WEB-INF"+File.separator+"classes";
		String contextPath = classes+File.separator+context;
		System.out.println("配置文件路径:::::::::::::"+contextPath);
		File file = new File(contextPath);
		//所有需要扫描的类
		List<String> javaList = new ArrayList<String>();
		if(file.exists()) {
			List<String> pacs = XmlHandle.annotationPac(file);
			System.out.println("需要扫描的包:::::::::"+pacs);
			for(String str : pacs) {
				File f = new File(str);
				System.out.println(f.listFiles());
				loop(f,javaList);
			}
			System.out.println("所有扫描的类:::::::::::::::::::");
			for(String str : javaList) {
				System.out.println(str);
			}
			System.out.println(":::::::::::::::::::::::::::");
		}else {
			System.out.println(contextPath+" 配置文件不存在");
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
			filePath = filePath.replace(".java", "");
			list.add(filePath);
		}
	}
	
}
