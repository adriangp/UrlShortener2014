package urlshortener2014.common.web;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.repository.ShortURLRepository;



//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = {WebAppContext.class})
//@WebAppConfiguration
public class UrlShortenerTests {

	private MockMvc mockMvc;

	@Mock
	private ShortURLRepository repository;
	
	@InjectMocks
	private UrlShortenerController urlShortener;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(urlShortener).build();
	}

	@Test
	public void thatRedirectToReturnsTemporaryRedirectIfKeyExists() throws Exception {
		when(repository.findByKey("someKey")).thenReturn(new ShortURL("someKey", "http://example.com/", null, null, null, 307));
		
		mockMvc.perform(get("/l{id}", "someKey"))
				.andDo(print())
				.andExpect(status().isTemporaryRedirect())
				.andExpect(redirectedUrl("http://example.com/"));
	}

	@Test
	public void thatRedirecToReturnsNotFoundIdIfKeyDoesNotExist() throws Exception {
		when(repository.findByKey("someKey")).thenReturn(null);
		
		mockMvc.perform(get("/l{id}", "someKey"))
				.andDo(print())
				.andExpect(status().isNotFound());
	}
	
	@Test
	public void thatShortenerCreatesARedirectIfTheURLisOK() throws Exception {
		configureTransparentSave();
		
		mockMvc.perform(post("/link").param("url", "http://example.com/"))
				.andDo(print())
				.andExpect(redirectedUrl("http://localhost/lf684a3c4"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.hash", is("f684a3c4")))
				.andExpect(jsonPath("$.uri", is("http://localhost/lf684a3c4")))
				.andExpect(jsonPath("$.target", is("http://example.com/")));
	}


	@Test
	public void thatShortenerFailsIfTheURLisWrong() throws Exception {
		configureTransparentSave();
		
		mockMvc.perform(post("/link").param("url", "someKey"))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	public void thatShortenerFailsIfTheRepositoryReturnsNull() throws Exception {
		when(repository.save(org.mockito.Matchers.any(ShortURL.class))).thenReturn(null);

		mockMvc.perform(post("/link").param("url", "someKey"))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	private void configureTransparentSave() {
		when(repository.save(org.mockito.Matchers.any(ShortURL.class))).then(new Answer<ShortURL>() {
			@Override
			public ShortURL answer(InvocationOnMock invocation)
					throws Throwable {
				return (ShortURL) invocation.getArguments()[0];
			}});
	}
}
