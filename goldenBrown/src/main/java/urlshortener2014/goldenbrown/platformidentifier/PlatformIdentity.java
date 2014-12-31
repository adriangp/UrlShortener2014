package urlshortener2014.goldenbrown.platformidentifier;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.bitwalker.useragentutils.Browser;
import nl.bitwalker.useragentutils.OperatingSystem;
import nl.bitwalker.useragentutils.UserAgent;
import nl.bitwalker.useragentutils.Version;

public class PlatformIdentity {
	private UserAgent agent;
	@JsonProperty("browser")
	private String browser;
	@JsonProperty("version")
	private String version;
	@JsonProperty("os")
	private String os;
	
	// Dummy constructor needed to be Jackson-parseable
	public PlatformIdentity(){}
	
	public PlatformIdentity(String us){
		this.agent = UserAgent.parseUserAgentString(us);
		
		this.browser = agent.getBrowser().toString();
		this.version = agent.getBrowserVersion().toString();
		this.os = agent.getOperatingSystem().toString();
	}
	
	@JsonProperty("browser")
	public String getBrowser() {
		return this.browser;
	}
	
	@JsonProperty("version")
	public String getVersion() {
		return this.version;
	}
	
	@JsonProperty("os")
	public String getOs() {
		return this.os;
	}
}
