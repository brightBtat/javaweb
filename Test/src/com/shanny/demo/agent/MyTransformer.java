package com.shanny.demo.agent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

/**
 * 
 * @author whitexiaosheng
 * @version 2019/8/19
 *
 */
public class MyTransformer implements ClassFileTransformer{

	final static String prefix = "\n long startTime = System.currentTimeMillis(); \n";
	final static String postfix = "\n long endTime = System.currentTimeMillis(); \n";
	
	//被处理的方法列表
	final static Map<String, List<String>> methodMap = new HashMap<String, List<String>>();
	
	public MyTransformer() {
		add("com.shanhy.demo.TimeTest.sayHello");
		add("com.shanhy.demo.TimeTest.sayHello2");
	}
	
	private void add(String methodString) {
		//类的全限定名
		String className = methodString.substring(0,methodString.lastIndexOf("."));
		//方法名
		String methodName = methodString.substring(methodString.lastIndexOf("."+1));
		
		List<String> list = methodMap.get(className);
		
		if(list == null) {
			list = new ArrayList<>();
			methodMap.put(className, list);
		}
		list.add(methodName);
		
	}

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		className = className.replace("/", ".");
		if(methodMap.containsKey(className)) {// 判断加载的classs的包路径是不是需要监控的类
			CtClass ctclass = null;
			try {
				ctclass = ClassPool.getDefault().get(className);// 使用全称，用于取得字节码类<使用javassist>
				for(String methodName : methodMap.get(className)) {
					String outputStr = "\n System.out.println(\"this method "+methodName+" cost:\" + (endTime-startTime) +\"ms.\");";
					CtMethod ctmethod = ctclass.getDeclaredMethod(methodName);// 得到这个实例
					String newMethodName = methodName + "$old";// 新定义一个方法叫做比如sayHello$old
					ctmethod.setName(newMethodName);// 将原来的方法名字修改
					
					// 创建新的方法，复制原来的方法，名字为原来的名字
					CtMethod newMethod = CtNewMethod.copy(ctmethod, methodName, ctclass, null);
					
					//构建新的方法体
					StringBuilder bodyStr = new StringBuilder();
					bodyStr.append("{");
					bodyStr.append(prefix);
					bodyStr.append(newMethodName + "$($$);\n");// 调用原有代码，类似method();($$)表示所有的参数
					bodyStr.append(postfix);
					bodyStr.append(outputStr);
					bodyStr.append("}");
					
					newMethod.setBody(bodyStr.toString());// 替换新方法
					ctclass.addMethod(newMethod);// 增肌新方法
				}
				return ctclass.toBytecode();
			}catch(Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}
		return null;
	}
	

}
