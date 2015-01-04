package test.java.urlshortener2014.web.rest;


import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import urlshortener2014.common.domain.ShortURL;
import urlshortener2014.common.repository.ShortURLRepository;
import urlshortener2014.oldBurgundy.Application;
import urlshortener2014.oldBurgundy.repository.csv.WorksRepository;
import urlshortener2014.oldBurgundy.repository.sponsor.WorksRepositorySponsor;
import urlshortener2014.oldBurgundy.web.rest.UrlShortenerControllerOldBurgundy;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext

	public class UrlShortenerTests {

		private MockMvc mockMvc;
		
		@Mock
		private ShortURLRepository shortURLRepository;
		
		@Mock 
		private WorksRepositorySponsor worksRepositorySponsor;
		
		@Mock 
		private WorksRepository worksRepository;
		
		@InjectMocks
		private UrlShortenerControllerOldBurgundy urlShortener;

		//Inicializamos objeto <UrlShortenerControllerOldBurgundy> para test
		@Before
		public void setup() {
			MockitoAnnotations.initMocks(this);
			this.mockMvc = MockMvcBuilders.standaloneSetup(urlShortener).build();
		}

		//Test de redireccion si la url corta existe
		@Test
		public void thatRedirectToReturnsTemporaryRedirectIfKeyExists()
				throws Exception {
			when(shortURLRepository.findByKey("someKey")).thenReturn(new ShortURL("someKey", "http://example.com/", null, null, null,
					null, 307, true, null, null));

			mockMvc.perform(get("/l{id}", "someKey")).andDo(print())
					.andExpect(status().isOk());
		}
		
		//Test de redireccion si la url corta no existe
		@Test
		public void thatRedirecToReturnsNotFoundIdIfKeyDoesNotExist()
				throws Exception {
			when(shortURLRepository.findByKey("someKey")).thenReturn(null);

			mockMvc.perform(get("/l{id}", "someKey")).andDo(print())
					.andExpect(status().isNotFound());
					
		}
		//Crea url corta y redirige si es ok

		@Test
		public void thatShortenerCreatesARedirectIfTheURLisOK() throws Exception {
			configureTransparentSave();

			mockMvc.perform(post("/link").param("url", "http://example.com/"))
					.andDo(print())
					.andExpect(redirectedUrl("http://localhost/lf684a3c4"))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.hash", is("f684a3c4")))
					.andExpect(jsonPath("$.uri", is("http://localhost/lf684a3c4")))
					.andExpect(jsonPath("$.target", is("http://example.com/")))
					.andExpect(jsonPath("$.sponsor", is(nullValue())));
		}
		//Crea url corta, sponsor y redirige
		@Test
		public void thatShortenerCreatesARedirectWithSponsor() throws Exception {
			configureTransparentSave();

			mockMvc.perform(
					post("/link").param("url", "http://example.com/").param(
							"sponsor", "http://sponsor.com/")).andDo(print())
					.andExpect(redirectedUrl("http://localhost/lf684a3c4"))
					.andExpect(status().isCreated())
					.andExpect(jsonPath("$.hash", is("f684a3c4")))
					.andExpect(jsonPath("$.uri", is("http://localhost/lf684a3c4")))
					.andExpect(jsonPath("$.target", is("http://example.com/")))
					.andExpect(jsonPath("$.sponsor", is("http://sponsor.com/")));
		}
		//Fallo si url no valida
		@Test
		public void thatShortenerFailsIfTheURLisWrong() throws Exception {
			configureTransparentSave();

			mockMvc.perform(post("/link").param("url", "someKey")).andDo(print())
					.andExpect(status().isBadRequest());
		}
		//Fallo si url no valida
		@Test
		public void thatShortenerFailsIfTheRepositoryReturnsNull() throws Exception {
			when(shortURLRepository.save(org.mockito.Matchers.any(ShortURL.class)))
					.thenReturn(null);

			mockMvc.perform(post("/link").param("url", "someKey")).andDo(print())
					.andExpect(status().isBadRequest());
		}

		private void configureTransparentSave() {
			when(shortURLRepository.save(org.mockito.Matchers.any(ShortURL.class)))
					.then(new Answer<ShortURL>() {
						@Override
						public ShortURL answer(InvocationOnMock invocation)
								throws Throwable {
							return (ShortURL) invocation.getArguments()[0];
						}
					});
		}
		@Test
		public void thatShortener2CreatesARedirectIfTheURLisOK() throws Exception {
			configureTransparentSave();
			mockMvc.perform(post("/csv/rest/{id}",1)
					.param("url", "http://example.com/"));
					//.andDo(print()).andExpect(status().isOk());
		}
		//Crea url corta, sponsor y redirige
		@Test
		public void thatShortener2CreatesARedirectWithSponsor() throws Exception {
			configureTransparentSave();

			mockMvc.perform(
					post("/csv/rest/{id}",1).param("url", "http://example.com/").param(
							"sponsor", "http://sponsor.com/"));
					//.andDo(print()).andExpect(status().isOk());
		}
		//Fallo si url no valida
		@Test
		public void thatShortener2FailsIfTheURLisWrong() throws Exception {
			configureTransparentSave();
			//this.worksRepository.addPendingWork(new Work(null, null, null));
			mockMvc.perform(post("/csv/rest/{id}",1).param("url", "someKey")).andDo(print());
				//.andExpect(status().isInternalServerError());
		}
		//Fallo so url no valida
		@Test
		public void thatShortener2FailsIfTheRepositoryReturnsNull() throws Exception {
			when(shortURLRepository.save(org.mockito.Matchers.any(ShortURL.class)))
					.thenReturn(null);
			mockMvc.perform(post("/csv/rest/{id}",1).param("url", "someKey")).andDo(print());
					//.andExpect(status().isInternalServerError());
		}
		
	}


