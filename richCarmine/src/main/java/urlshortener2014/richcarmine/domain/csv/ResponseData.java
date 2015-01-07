package urlshortener2014.richcarmine.domain.csv;

import java.util.List;

/**
 * response used to answer back on any call
 */
public class ResponseData {
    private String uri;
    private long consumedTime;
    private List<CSVContent> csv;

    public ResponseData() {
    }

    public long getConsumedTime() {
        return consumedTime;
    }

    public void setConsumedTime(long consumedTime) {
        this.consumedTime = consumedTime;
    }

    public String getUri() {
        return uri;
    }

    public List<CSVContent> getCsv() {
        return csv;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setCsv(List<CSVContent> csv) {
        this.csv = csv;
    }
}
