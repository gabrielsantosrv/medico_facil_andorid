package com.medicofacil.medicofacilapp.classesDBO;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;


public class CustomTimestampDeserializer extends JsonDeserializer<Timestamp>{
  
  @Override
  public Timestamp deserialize(JsonParser jsonParser,DeserializationContext context)throws IOException,JsonProcessingException{
    Timestamp dataHora = null;
	try{
	  dataHora = new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(jsonParser.getText()).getTime());
    }
    catch (Exception e){
      e.printStackTrace();
    }
	return dataHora;
  }
}
