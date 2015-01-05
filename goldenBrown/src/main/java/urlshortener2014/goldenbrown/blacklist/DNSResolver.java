package urlshortener2014.goldenbrown.blacklist;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DNSResolver {
	private static final Logger logger = LoggerFactory.getLogger(BlackListController.class);
	
	private String host; 
	private String antispamSites[]; 
	private boolean result;
	private ExecutorService taskExecutor;
	
	public DNSResolver(String host, String[] antispamSites){
		this.host = host;
		this.antispamSites = antispamSites;
		this.result = false;
		this.taskExecutor = Executors.newFixedThreadPool(antispamSites.length);
	}
	
	public synchronized void setResult(boolean res){
		this.result = res;
	}
	public boolean doQuery(){
		String domain="";
		for (int i=0; i<antispamSites.length; i++){
			domain = host+"."+antispamSites[i]+".";
			taskExecutor.execute(new DNSResolverThread(i, domain, this));
		}
		taskExecutor.shutdown();
		try {
			taskExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	
	private class DNSResolverThread implements Runnable {	
		private int id;
		private String domain;
		private DNSResolver dnsResolver;
		
		public DNSResolverThread(int id, String domain, DNSResolver dnsResolver) {
			this.id = id;
			this.domain = domain;
			this.dnsResolver = dnsResolver;
		}

		@Override
		public void run() {
			String addr;
			try {
				addr = InetAddress.getByName(domain).getHostAddress();
				if (addr != null){
					dnsResolver.setResult(true);
					logger.info("Thread "+id+": It's in blacklist");
				}
			} catch (UnknownHostException e) {
				logger.info("Thread "+id+": "+e.getMessage());
			}
		}
		
	}
}
