package org.unibl.etf.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

@Path("/token")
public class TokenApiService {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response generateToken(String object) {
		String token=TokenGeneratorService.generateToken(object);
		if(token!=null) {
			JSONObject obj=new JSONObject();
			obj.put("token", token);
			return Response.status(200).entity(obj.toString()).build();
		}else {
			return Response.status(404).build();
		}
	}
	
}
