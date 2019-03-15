import java.awt.List;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class WordCount{
	public static int countChar(File file) throws IOException {//�����ַ���
		int charnum=0;
		BufferedReader reader=new BufferedReader(new FileReader(file));
		int c;
		while((c=reader.read())!=-1) {
			if (isChar(c)) {
				charnum++;
			}	
		}
		reader.close();
		return charnum;
	}
	public static int countLine(File file) throws IOException {//��������
		int linenum=0;
		BufferedReader reader=new BufferedReader(new FileReader(file));
		String s=null;
		while((s=reader.readLine())!=null) {
			if (!Pattern.matches("\\s*", s)) {
				linenum++;	
			}
		}
		reader.close();
		return linenum;
	}
	public static int countWord(File file) throws IOException{//���㵥����
		int wordnum=0;
		BufferedReader reader=new BufferedReader(new FileReader(file));
		String s=null;
		while((s=reader.readLine())!=null) {
			String[] strings=toWordList(s);//�ָ�
			for(int i=0;i<strings.length;i++) {
				if (isWord(strings[i])) {
					wordnum++;
				}
			}
		}
		return wordnum;
	}
	
	
	public static String[] outPutTop10(File file) throws IOException {//���top10����
		HashMap<String, Integer> wordMap=toWordMap(file);
		
        ArrayList<Map.Entry<String,Integer>> list = new ArrayList<Map.Entry<String,Integer>>(wordMap.entrySet());
        Collections.sort(list,new Comparator<Map.Entry<String,Integer>>() {
            //��������
			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				// TODO Auto-generated method stub
				if (o1.getValue().compareTo(o2.getValue())==0) {//�����Ƶ��ͬ				
					return -1;
				}
				 return o2.getValue().compareTo(o1.getValue());
			}
        });
        int num=0;
        String[] top10=new String[10];//����TOP10
        for(Map.Entry<String,Integer> mapping:list){ 
        	if (num==10) {
				break;
			}
        	top10[num]="<"+mapping.getKey()+">"+":"+mapping.getValue();
        	num++;
        } 
        return top10;
    }
	
	
	public static HashMap<String, Integer> toWordMap(File file) throws IOException {//��txt�ļ����ݹ���Ϊһ��HashMap�ṹ
		HashMap<String, Integer> hashMap=new HashMap<>();
		BufferedReader reader=new BufferedReader(new FileReader(file));
		String s=null;
		int index;
		while((s=reader.readLine())!=null) {
			index=0;
			String[] strings=toWordList(s);//�ָ�
			while(index<strings.length) {
				if (isWord(strings[index])) {
					String lowerstring=strings[index].toLowerCase();//����תΪСд
					if (hashMap.containsKey(lowerstring)) {//�ж��Ƿ��ظ�
						hashMap.put(lowerstring, hashMap.get(lowerstring)+1);//����ظ���Ƶ��1
					}
					else {
						hashMap.put(lowerstring, 1);//������ʲ��ظ���ֵΪ1
					}
				}
				index++;
			}
		}
		reader.close();
		return hashMap;
	}
	public static String[] toWordList(String s) {//���ַ���ת��Ϊ׼�����б�
		Pattern pattern2=Pattern.compile("[^A-Za-z0-9]");//ƥ�����зǵ����ַ�
		String[] strings=pattern2.split(s);//�ָ�����
		return strings;
	}
	
	public static boolean isChar(int c) {
		if (c>=0&&c<=127&&c!=13) {
			return true;
		}
		return false;
	}
	public static boolean isWord(String s) {
		return Pattern.matches("[A-Za-z]{4,}[a-zA-Z0-9]*", s);
	}
	
	//IO
	public static void outCount(File input,File output) throws IOException {//��������result.txt�ļ�
		if (!output.exists()) {//����ļ��������򴴽�
			output.createNewFile();
		}
		BufferedWriter writer=new BufferedWriter(new FileWriter(output));
		writer.write("characters:"+WordCount.countChar(input)+"\r\n");
		writer.write("words:"+WordCount.countWord(input)+"\r\n");
		writer.write("lines:"+WordCount.countLine(input)+"\r\n");
		String[] top10=WordCount.outPutTop10(input);
		for(int i=0;i<10;i++) {
			if (top10[i]!=null) {
				writer.write(top10[i]+"\r\n");
			}
		}
		writer.close();
	}
}

