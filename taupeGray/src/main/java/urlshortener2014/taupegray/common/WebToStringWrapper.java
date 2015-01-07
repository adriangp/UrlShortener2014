package urlshortener2014.taupegray.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.*;

public class WebToStringWrapper {
	String path;
	HttpServletRequest request;
	HttpServletResponse response;
	public WebToStringWrapper(String path, HttpServletRequest request, HttpServletResponse response) {
		this.path = path;
		this.request = request;
		this.response = response;
		
	}
	
	/**
	 * Gets the content of a local webpage as String.
	 * @return String containing the webpage requested
	 */
	public String getContent() {
		
		HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(response) {
            private final StringWriter sw = new StringWriter();

            @Override
            public PrintWriter getWriter() throws IOException {
                return new PrintWriter(sw);
            }

            @Override
            public String toString() {
            	sw.flush();
                return sw.toString();
            }
        };
        try {
			request.getServletContext().getRequestDispatcher(path).include(request, responseWrapper);
			return responseWrapper.toString();
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return null;
	}
}
