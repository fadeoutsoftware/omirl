package it.fadeout.omirl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.owlike.genson.Genson;

@Provider
public class GensonProvider implements ContextResolver<Genson>{
	  private final Genson genson = new Genson.Builder().setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")).create();

	    @Override
	   public Genson getContext(Class<?> type) {
	     return genson;
	   }
}
