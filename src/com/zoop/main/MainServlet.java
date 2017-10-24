package com.zoop.main;

import java.io.File;

//处理web请求找到对应的controller
public class MainServlet {

	public MainServlet(){
		init();
	}
	
	//实例化注解的类
	private void init(){
		String dir = System.getProperty("user.dir")+"\\src";
		System.out.println(dir);
		File file = new File(dir);
		System.out.println(file.listFiles());
	}
	
}
