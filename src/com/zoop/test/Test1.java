package com.zoop.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

public class Test1 {

	public static void main(String[]args) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", "jiji");
		map.put("name", "nnnjiji");
		list.add(map);
		System.out.println(JSON.toJSONString(map));
		int[] arr = {1,55,8,3,6,1,8,6,2};
		exec(arr);
		System.out.println(Arrays.toString(arr));
	}
	
	public static void exec(int[] arr) {
		for(int i = 1; i < arr.length; i++) {
			for(int j = 0; j < arr.length-i; j++) {
				if(arr[j] >= arr[j+1]) {
					int temp = arr[j];
					arr[j] = arr[j+1];
					arr[j+1] = temp;
				}
			}
		}
	}
	
}
