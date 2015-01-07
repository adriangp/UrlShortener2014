package urlshortener2014.bangladeshgreen;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
import urlshortener2014.common.domain.ShortURL;
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
		when(shortURLRepository.save(org.mockito.Matchers.any(ShortURL.class)))
				.then(new Answer<ShortURL>() {
					@Override
					public ShortURL answer(InvocationOnMock invocation)
							throws Throwable {
						ShortURL su = (ShortURL) invocation.getArguments()[0];
						// Do something with su if needed
						return su;
					}
				});
	}

	@Test
	public void urlSegura() throws Exception {

		mockMvc.perform(post("/link").param("url", "http://www.google.es"))
				.andDo(print()).andExpect(jsonPath("$.safe", is(true)));
	}

	@Test
	public void urlNoSegura() throws Exception {

		mockMvc.perform(post("/link").param("url", "http://ianfette.org"))
				.andDo(print()).andExpect(jsonPath("$.safe", is(false)));
	}

	//Para la correcta comprobacion de este test, se debe mirar el log.
	//Se ha insertado una cabecera correspondiente al hacer una peticion con Chrome desde Windows
	@Test
	public void comprobarCabeceras() throws Exception {
		when(shortURLRepository.findByKey("someKey")).thenReturn(someUrl());

		mockMvc.perform(
				get("/l{id}", "someKey")
						.header("User-Agent",
								"Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36"))
				.andDo(print());
	}
	
	public static ShortURL someUrl() {
		return new ShortURL("someKey", "http://example.com/", null, null, null,
				null, 307, true, null, null);
	}

}
