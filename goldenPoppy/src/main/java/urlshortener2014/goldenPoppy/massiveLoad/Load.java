package urlshortener2014.goldenPoppy.massiveLoad;

import java.util.ArrayList;
import java.util.List;

public class Load implements Runnable{

	List<String> longUrls;
	
	public Load(List<String> list){
		this.longUrls = new ArrayList<String>();
		for (String s : list)
			longUrls.add(s);
	}
	@Override
	public void run() {
		System.out.println("Holaaa");
		for (String s : longUrls){
			System.out.println(s);
			System.out.println(Thread.currentThread().getName());
		}
	}	
}