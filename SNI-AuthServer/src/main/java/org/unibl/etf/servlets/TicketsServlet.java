package org.unibl.etf.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.unibl.etf.model.dto.User;
import org.unibl.etf.util.Configuration;
import org.unibl.etf.util.TicketsUtil;

import com.google.gson.Gson;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Servlet implementation class TicketsServlet
 */
@WebServlet("/tickets")
public class TicketsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TicketsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String jsonBody = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(
	            Collectors.joining("\n"));
		if(jsonBody!=null && !"".equals(jsonBody)) {
			//check if valid

			JSONObject obj=new JSONObject(jsonBody);
			if(!obj.has("ticket")){
				response.setStatus(404);
				return;
			}
			String ticket=obj.getString("ticket");
			User user=null;
			
			synchronized(TicketsUtil.mapLocker) {
				//check if ticket is valid
				if(TicketsUtil.tickets.containsKey(ticket)) {
					user=TicketsUtil.tickets.get(ticket);
					TicketsUtil.tickets.remove(ticket);
				}
			}
			
			if(user!=null) { //means that ticket is still valid
				String jwtToken="";
				try {
					PrivateKey privateKey=getPrivateKey();
					Instant now=Instant.now();
					SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;
					
					jwtToken=Jwts.builder().claim("username",user.getUsername())
											.setSubject("auth")
											.setId(UUID.randomUUID().toString())
											.setIssuedAt(Date.from(now))
											.setExpiration(Date.from(now.plus(5l, ChronoUnit.MINUTES)))
											.signWith(signatureAlgorithm, privateKey)
											.compact();
					
				} catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
					System.out.println("Private key exeption");
				}
				
				
				response.setContentType("text/plain");
				response.setStatus(200);
				PrintWriter pw=response.getWriter();
				pw.write(jwtToken);
				pw.flush();
				pw.close();
			}
			else {
				response.setStatus(404);
			}
		}
	}

	private PrivateKey getPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes=Files.readAllBytes(Paths.get(Configuration.privateKeyPath));
		
		PKCS8EncodedKeySpec spec=new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf=KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
	}
}
