package urlshortener2014.bangladeshgreen.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.Entity;
//import javax.ws.rs.core.MediaType;
import javax.servlet.http.Part;

@WebServlet("/Funciones")
@MultipartConfig
public class Funciones extends HttpServlet {

	// Funciones para coger el fichero y mandarlo a REST y otra para
	// pasar a la funcion REST shortener, sin tener que recargar la pagina
	// que eso se hace con AJAX, mirar pagina
	// http://jarroba.com/ajax-con-jsp-y-servelts/

	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Funciones() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
//
//		   String description = request.getParameter("description"); // Retrieves <input type="text" name="description">
//		    Part filePart = request.getPart("file"); // Retrieves <input type="file" name="file">
//		    String fileName = filePart.getSubmittedFileName();
//		    InputStream fileContent = filePart.getInputStream();
//		
		
		System.out.println("llefgoooooo");
		
		String description = request.getParameter("description"); // Retrieves <input type="text" name="description">
	    Part filePart = request.getPart("file"); // Retrieves <input type="file" name="file">
	    InputStream fileContent = filePart.getInputStream();
		
	    System.out.println(filePart.getSize());
		String param = request.getParameter("file");
		System.out.println(param);

		// ClientBuilder.newClient().target("http://localhost:8080/list")
		// .request(MediaType.APPLICATION_JSON)
		// .post(Entity.entity(json, MediaType.APPLICATION_JSON));

//		response.sendRedirect("index.jsp");
	}
}
