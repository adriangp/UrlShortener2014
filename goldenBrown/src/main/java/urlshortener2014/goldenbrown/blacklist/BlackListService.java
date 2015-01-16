package urlshortener2014.goldenbrown.blacklist;

import org.springframework.cache.annotation.Cacheable;

/**
 * This class create an object that query the third databases if a host is in a blacklist
 * @author: Jorge,Javi,Gabi
 * @version: 08/01/2015
 */
public class BlackListService {

	/**
	 * This method create an object that query the third databases if a host is in a blacklist
	 * @param host to query if in a blacklist
	 * @return true if it is in a black list or false if not
	 */
	@Cacheable("blcache")
	public  Boolean isBlackListed(String host){
		final String[] antispamSites = {"zen.spamhaus.org",
										"multi.surbl.org",
										"black.uribl.com"};
		DNSResolver dnsResolver = new DNSResolver(host, antispamSites);
		return dnsResolver.doQuery();
	}

}
