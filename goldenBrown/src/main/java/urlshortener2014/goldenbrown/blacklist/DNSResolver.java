package urlshortener2014.goldenbrown.blacklist;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * This class query to antispamSites (third databases) to know if a given host is a blacklist
 * or not
 * @author: Jorge,Javi,Gabi
 * @version: 08/01/2015
 */
public class DNSResolver {
	private static final Logger logger = LoggerFactory.getLogger(BlackListController.class);
	
	private String host; 
	private String antispamSites[]; 
	private boolean result;
	private ExecutorService taskExecutor;
	/**
	 * Public creator of the class
	 * @param host to know if it is in a blacklist
	 * @param antispamSites sites to query about the blacklist
	 */
	public DNSResolver(String host, String[] antispamSites){
		this.host = host;
		this.antispamSites = antispamSites;
		this.result = false;
		this.taskExecutor = Executors.newFixedThreadPool(antispamSites.length);
	}
	
	public synchronized void setResult(boolean res){
		this.result = res;
	}
	/**
	 * This method create a thread for every antispamSite that proves if the host is in the blacklist
	 * @return true if it is in a black list or false if not
	 */
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
	
	
	/**
	 * This class is a thread that prove in the given antispamsite if the host is in the blacklist
	 * or not
	 * @author: Jorge,Javi,Gabi
	 * @version: 08/01/2015
	 */
	private class DNSResolverThread implements Runnable {	
		private int id;
		private String host;
		private String antispamsite;
		private DNSResolver dnsResolver;
		/**
		 * Public creator of the class
		 * @param id id of the site
		 * @param host hos to know if it is in the blacklist
		 * @param antispamsite URL of the antispam site
		 * @param dnsResolver object DNRResolver to put the answer about if the host is safe or not
		 */
		public DNSResolverThread(int id, String host, String antispamsite, DNSResolver dnsResolver) {
			this.id = id;
			this.host = host;
			this.antispamsite = antispamsite;
			this.dnsResolver = dnsResolver;
		}
		/**
		 * Main method of the class. This method prove if a given host if in the blacklist of the
		 * given antispam site
		 */
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
