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
		for (int i=0; i<antispamSites.length; i++){
			taskExecutor.execute(new DNSResolverThread(i, host, antispamSites[i], this));
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
		private String host;
		private String antispamsite;
		private DNSResolver dnsResolver;
		
		public DNSResolverThread(int id, String host, String antispamsite, DNSResolver dnsResolver) {
			this.id = id;
			this.host = host;
			this.antispamsite = antispamsite;
			this.dnsResolver = dnsResolver;
		}

		@Override
		public void run() {
			String ip = "", parsedIp = "", domain = "", blacklist_res = "";
			ip = resolveDNS(host); // Get the ip
			if (ip != null){
				parsedIp = parseIp(ip);
				domain = parsedIp+"."+antispamsite;
				blacklist_res = resolveDNS(domain); // Query to the antispamsite
				if (blacklist_res != null){
					this.dnsResolver.setResult(true);
					logger.info("Thread "+id+": It is in a blacklist -> "+domain);
				}
			}
			else{
				this.dnsResolver.setResult(false);
			}
		}
		
		private String parseIp(String ip) {
			String[] parts = ip.split("\\.");
			return parts[3]+"."+parts[2]+"."+parts[1]+"."+parts[0];
		}

		private String resolveDNS(String addr){
			String res_addr;
			try {
				res_addr = InetAddress.getByName(addr).getHostAddress();
				if (res_addr != null){
					return res_addr;
				}
				else{
					return null;
				}
			} catch (UnknownHostException e) {
				return null;
			}
		}
		
	}
}
