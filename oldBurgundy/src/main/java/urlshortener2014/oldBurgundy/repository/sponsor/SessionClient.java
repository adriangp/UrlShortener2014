package urlshortener2014.oldBurgundy.repository.sponsor;
import org.springframework.web.socket.WebSocketSession;
public class SessionClient {
		
		private int id = -1;
		private WebSocketSession session;
			

		public SessionClient(int id,WebSocketSession session) {
			this.setId(id);
			this.setSession(session);
		}


		public WebSocketSession getSession() {
			return session;
		}


		public void setSession(WebSocketSession session) {
			this.session = session;
		}


		public int getId() {
			return id;
		}


		public void setId(int id) {
			this.id = id;
		}
		

}
