package com.zoop.test;

import com.zoop.annotation.Autowired;
import com.zoop.annotation.Controller;
import com.zoop.annotation.RequestMapping;

@Controller
public class TestController {

	@Autowired
	private TestService testService;
	
	@RequestMapping(value="/list")
	public void exec(){
		if(testService == null){
			System.out.println("testService is null");
		}else{
			testService.exec();
		}
	}
	
}
