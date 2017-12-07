package com.zoop.main;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class XmlHandle {

	//获得需要扫描的包
	@SuppressWarnings("unchecked")
	public static List<String> annotationPac(File file){
		List<String> pac = new ArrayList<String>();
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(file);
			Element element = document.getRootElement();
			Iterator<Element> iterator = element.elementIterator();
			while(iterator.hasNext()) {
				Element node = iterator.next();
				if(node.getName().equals("scan")) {//扫描注解的标签
					List<Attribute> attributes = node.attributes();
					for(Attribute attribute: attributes) {
						pac.add(attribute.getValue());
					}
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return pac;
	}
	
}
