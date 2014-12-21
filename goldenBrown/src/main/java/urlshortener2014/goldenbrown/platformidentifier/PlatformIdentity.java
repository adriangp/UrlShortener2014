package urlshortener2014.goldenbrown.platformidentifier;

import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.OperatingSystem;
import nl.bitwalker.useragentutils.UserAgent;
import nl.bitwalker.useragentutils.Version;

public class PlatformIdentity {
	private UserAgent agent;
//	private Browser browser;
//	private Version version;
//	private OperatingSystem os;
	
	public PlatformIdentity(String userAgentString){
		this.agent = UserAgent.parseUserAgentString(userAgentString);
	}

	public Browser getBrowser() {
		return agent.getBrowser();
	}

	public Version getVersion() {
		return  agent.getBrowserVersion();
	}

	public OperatingSystem getOs() {
		return agent.getOperatingSystem();
	}
}
