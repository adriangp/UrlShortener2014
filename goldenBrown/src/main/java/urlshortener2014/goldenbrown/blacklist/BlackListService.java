package urlshortener2014.goldenbrown.blacklist;

import org.springframework.cache.annotation.Cacheable;

public class BlackListService {

	/**
	 * @param host
	 * @return
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
