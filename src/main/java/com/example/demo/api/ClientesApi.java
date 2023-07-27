package com.example.demo.api;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

@RestController
public class ClientesApi extends ResponseEntityExceptionHandler{

    
	@RequestMapping(value="/cliente", method=RequestMethod.GET)
	//@ExceptionHandler(Exception.class)
	@ExceptionHandler(HttpClientErrorException.class)
    public String getById(@RequestParam char tipoDocumento, @RequestParam String numeroDocumento) throws StreamReadException, DatabindException, IOException{
		String respuesta = "Ok";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");

        // Read the JSON file.
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(new File("db.json"), Map.class);
        map.remove("posts");
        // Add a new employee to the map.
        Map<String, String> clte = new HashMap<>();
        clte.put("primerNombre", "Pedro");
        clte.put("segundoNombre", "Pablo");
        clte.put("primerApellido", "Perez");
        clte.put("segundoApellido", "Perdomo");
        clte.put("telefono", "+57 311 222 3344");
        clte.put("direccion", "Suba");
        clte.put("ciudadResidencia", "Bogota");
        map.put("posts", Arrays.asList(map.get("posts"), clte));
        // Write the map to a JSON file.
        mapper.writeValue(new File("db.json"), map);    
	        HttpEntity<String> entity = new HttpEntity<>(headers);
	        ResponseEntity<String> response = restTemplate.exchange("http://localhost:3000/posts", HttpMethod.GET, entity, String.class);
	        if (response.getStatusCode().is2xxSuccessful()) {
	        	if (tipoDocumento == 'C' && numeroDocumento.equals("23445322")) {
	        		String responseBody = response.getBody();
	        		respuesta = responseBody;
	        	} else {
	        		respuesta = "No encontrado " + response.getStatusCode();
	        	}	
		    } else if (response.getStatusCode().is4xxClientError()) {
		    	if (response.getStatusCodeValue() == 400) {
		    		System.out.println("Error 400: " + response.getStatusCode());
		    	} else if (response.getStatusCodeValue() == 402) {
		    		System.out.println("Error 402: " + response.getStatusCode());
		    	}
	    	} else if (response.getStatusCodeValue() == 500){
	    		System.out.println("Error 500: " + response.getStatusCode());
	    	}
        
        return respuesta;
    }

}
