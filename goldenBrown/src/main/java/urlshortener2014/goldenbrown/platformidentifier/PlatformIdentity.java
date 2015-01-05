package urlshortener2014.goldenbrown.platformidentifier;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.bitwalker.useragentutils.Browser;//TODO REMOVE
import nl.bitwalker.useragentutils.OperatingSystem;//TODO REMOVE
import nl.bitwalker.useragentutils.UserAgent;
import nl.bitwalker.useragentutils.Version;//TODO REMOVE
/**
 * Web service that consist of read the headers User-Agent of the request HTTP and about it,
 * clasify navigator and platform of the click. For that, we use the library UserAgentUtils 
 * for fill better information of the request.
 * @author: Jorge,Javi,Gabi
 * @version: 08/01/2015
 */
public class PlatformIdentity {
	private UserAgent agent;
	@JsonProperty("browser")
	private String browser;
	@JsonProperty("version")
	private String version;
	@JsonProperty("os")
	private String os;

	/**
	 * Dummy constructor needed to be Jackson-parseable
	 */
	public PlatformIdentity(){}
	
	/**
	 * Method that uses the class UserAgent from UserAgentUtils package identifier the navigator
	 * and the platform of the user.
	 * @param us contain the User-Agent
	 * @throws IllegalArgumentException
	 */
	public PlatformIdentity(String us) throws IllegalArgumentException{
		if (us != null){
			this.agent = UserAgent.parseUserAgentString(us);
			this.os = agent.getOperatingSystem().toString();
			this.browser = agent.getBrowser().toString();
			if(agent.getBrowserVersion() != null){
				this.version = agent.getBrowserVersion().toString();
			}
			else{
				this.version = "UNKNOWN";
			}
		}
		else{
			new IllegalArgumentException("User-Agent cannot be null.");
		}
	}
	/**
	 * @return the browser information
	 */
	@JsonProperty("browser")
	public String getBrowser() {
		return this.browser;
	}
	/**
	 * @return the version information
	 */
	@JsonProperty("version")
	public String getVersion() {
		return this.version;
	}
	/**
	 * @return the OS information
	 */
	@JsonProperty("os")
	public String getOs() {
		return this.os;
	}
}
