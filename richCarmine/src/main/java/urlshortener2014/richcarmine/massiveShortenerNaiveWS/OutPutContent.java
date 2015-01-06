package urlshortener2014.richcarmine.massiveShortenerNaiveWS;

import org.json.JSONObject;

public class OutPutContent {

    private String shortenedURL;

    public OutPutContent(String shortenedURL) {

        this.shortenedURL = shortenedURL;
    }

    public String getShortenedURL() {
        return shortenedURL;

    }

    public void setShortenedURL(String shortenedURL) {
        this.shortenedURL = shortenedURL;
    }

    @Override
    public String toString() {
        return "URLOutput{" +
                "shortenedURL='" + shortenedURL + '\'' +
                '}';
    }
}
