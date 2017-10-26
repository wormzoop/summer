package com.zoop.test;

import java.util.Map;

import com.zoop.annotation.Autowired;
import com.zoop.annotation.Controller;
import com.zoop.annotation.RequestMapping;

@Controller
public class TestController {

	@Autowired
	private TestService testService;
	
	@RequestMapping(value="/list")
	public Map<String, Object> exec(){
		return testService.exec();
	}
	
}
