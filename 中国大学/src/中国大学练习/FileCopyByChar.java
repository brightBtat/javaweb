package 中国大学练习;

import java.io.*;

public class FileCopyByChar {

	public static void main(String[] args) {
		int record=0;
		try
		{
			FileReader input =new FileReader("E:\\eclipse\\中国大学课堂测验\\src\\中国大学课堂测验\\FileCopyByChar.java");
			BufferedReader br=new BufferedReader(input);
			FileWriter output =new FileWriter("E:\\eclipse\\中国大学课堂测验\\src\\中国大学课堂测验\\temp.txt");
			BufferedWriter bw = new BufferedWriter(output);
			
			//每次读取输出一个位置
//			char c=input.read();			
//			while(c!=-1)
//			{
//				output.write(c);
//				System.out.print((char)c);
//				c=input.read();
//				record++;
//			}
//			input.close();
//			output.close();
			//每次读取输出一行
			String s = br.readLine();			
			while ( s!=null ) {
				bw.write(s);
				bw.newLine();
				System.out.println(s);
				s = br.readLine();
				record++;
			}			
			br.close();
			bw.close();
		}
		catch(IOException e){
			System.out.println(e);
		}
		System.out.println(record);
	}

}
