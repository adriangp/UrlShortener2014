package urlshortener2014.bangladeshgreen;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static urlshortener2014.common.web.fixture.ShortURLFixture.someUrl;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import urlshortener2014.bangladeshgreen.web.UrlShortenerControllerWithLogs;
import urlshortener2014.common.repository.ClickRepository;
import urlshortener2014.common.repository.ShortURLRepository;

public class UrlShortenerTests {

	private MockMvc mockMvc;

	@Mock
	private ShortURLRepository shortURLRepository;

	@Mock
	private ClickRepository clickRespository;

	@InjectMocks
	private UrlShortenerControllerWithLogs urlShortenerWL;

	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(urlShortenerWL).build();
	}
	
	@Test
	public void thatShortenerCreatesARedirectIfTheURLisOK() throws Exception {
		mockMvc.perform(post("/link")
				.param("url", "http://www.google.com"))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.target", is("http://example.com/")));
	}
}