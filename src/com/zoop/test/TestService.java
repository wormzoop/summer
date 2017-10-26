package com.zoop.test;

import java.util.HashMap;
import java.util.Map;

import com.zoop.annotation.Service;

@Service
public class TestService {

	public Map<String, Object> exec(){
		System.out.println("exec");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", "1212121");
		map.put("name", "454jiji");
		return map;
	}
	
}
