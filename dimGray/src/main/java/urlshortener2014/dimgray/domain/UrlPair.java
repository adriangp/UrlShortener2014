package urlshortener2014.dimgray.domain;

import java.net.URI;

/**
 * Esta clase contiene una url junto a su url acortada.
 * @author Ivan
 *
 */

public class UrlPair {
	
	private String url;
	private URI shortenedUrl;
	
	/**
     * Constructor de la clase vacío.
     */
	public UrlPair() {
	}
	
	/**
	 * Constructor de la clase.
	 * @param url Url sin acortar.
	 * @param shortenedUrl Url acortada.
	 */
	public UrlPair(String url, URI shortenedUrl) {
		this.url = url;
		this.shortenedUrl = shortenedUrl;
	}

	/**
	 * Método que devuelve la url de la clase.
	 * @return la URL de la clase.
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Asigna la url a un objeto de esta clase.
	 * @param url url a asignar.
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Método que devuelve la url acortada de la clase.
	 * @return la URL acortada de la clase.
	 */
	public URI getShortenedUrl() {
		return shortenedUrl;
	}

	/**
	 * Asigna la url acortada a un objeto de esta clase.
	 * @param shortenedUrl url acortadaa asignar.
	 */
	public void setShortenedUrl(URI shortenedUrl) {
		this.shortenedUrl = shortenedUrl;
	}

}
