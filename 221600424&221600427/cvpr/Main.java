package jspider;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Main {
	final static int threadnum = 100;													
	public static void FileOutput(String path) throws IOException {
		PaperList pList = runspider();
		File file = new File(path);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fWriter = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bWriter = new BufferedWriter(fWriter);
		bWriter.write("");
		for (int i = 0; i < pList.Length(); i++) {
			String rank = i + "\r\n";
			String title = "Title: " + pList.get(i).get(0) + "\r\n";
			String abs = "Abstract: " + pList.get(i).get(1) + "\r\n\r\n";
			bWriter.append(rank);
			bWriter.append(title);
			bWriter.append(abs);
		}
		bWriter.close();
	}
	private static Document getDoc() throws IOException{
		String url = "http://openaccess.thecvf.com/CVPR2018.py";
		Document doc = Jsoup.connect(url)
				.userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
				.maxBodySize(0)
				.timeout(30000)
				.get();
		
		return doc;
	}
	private static PaperList runspider() {
		PaperList pList = new PaperList();
		try {
			
			Document document = getDoc();
			Elements contentdiv = document.select("div#content");
			Elements paperdt = contentdiv.select("dt.ptitle > a");
			int dealnum = paperdt.size() / threadnum + 1;
			//System.out.println(dealnum);
			ArrayList<Thread> threads = new ArrayList<Thread>();
			for (int i = 0; i < threadnum; i++) {
				Thread newThread = new Thread(new absThread(i, dealnum, paperdt.size(), paperdt, pList));
				newThread.start();
				threads.add(newThread);
			}
			for (int i = 0; i < threads.size(); i++) {
				threads.get(i).join();
			}
			//System.out.println("test");
			/*Iterator<org.jsoup.nodes.Element> item = paperdt.iterator();
			while (item.hasNext() && pList.Length()<10) {
				org.jsoup.nodes.Element e = (org.jsoup.nodes.Element) item.next();
				String title = e.text();
				String abs = absspider("http://openaccess.thecvf.com/" + e.attr("href"));
				pList.add(title, abs);
			}*/
			//System.out.println("123456789"+pList.Length());
		} catch (Exception e) {
			System.out.println("Error!");
			e.printStackTrace();
			// TODO: handle exception
		}
		return pList;
	}
	/*private static String absspider(String Url) throws IOException{
		Document doc = Jsoup.connect(Url)
				.userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
				.maxBodySize(0)
				.get();
		String absString = doc.select("div#content dd > div#abstract").text();
		return absString;
	}*/
	public static void main(String[] args) {
		try {
			Main.FileOutput(".//result.txt");
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
class absThread implements Runnable{
	private int No,min,max;
	private Elements papersElements;
	private static Lock lock = new ReentrantLock();
	PaperList pList;
	public absThread(int n,int dealnum,int m,Elements papers,PaperList p) {
		No = n;
		min = n * dealnum;
		max = (n + 1) * dealnum < m?(n + 1) * dealnum:m;
		papersElements = papers;
		pList = p;
		// TODO Auto-generated constructor stub
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		for (int i = min; i < max; i++) {
			try {
				String title = papersElements.get(i).text();
				//System.out.println("title: " + title);
				String url = "http://openaccess.thecvf.com/" + papersElements.get(i).attr("href");
				String abs = absThread.absspider(url);
				//System.out.println("abstract: " + abs);
				lock.lock();
				pList.add(title, abs);
				lock.unlock();
			} catch (Exception e) {
				System.out.println("Error!");
				e.printStackTrace();
				// TODO: handle exception
			}
		}
	}
	private static String absspider(String Url) throws IOException{
		Document doc = Jsoup.connect(Url)
				.userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36")
				.maxBodySize(0)
				.timeout(100000)
				.get();
		String absString = doc.select("div#content dd > div#abstract").text();
		return absString;
	}
}
class PaperList{
	private ArrayList<String> title;
	private ArrayList<String> abs;
	private int length;
	public PaperList(){
		title = new ArrayList<String>();
		abs = new ArrayList<String>();
		length = 0;
	}
	public void add(String t,String a) {
		title.add(t);
		abs.add(a);
		length++;
	}
	public ArrayList<String> get(int i) {
		ArrayList<String> aList = new ArrayList<String>();
		aList.add(title.get(i));
		aList.add(abs.get(i));
		return aList;
	}
	public int Length() {
		return length;
	}
}

