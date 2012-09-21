package com.vaushell.treetasker.module;

import java.io.IOException;
import java.util.Date;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class GsonDateAdapter
	extends TypeAdapter<Date>
{

	@Override
	public Date read(
		JsonReader reader )
		throws IOException {
		if ( reader.peek() == JsonToken.NULL )
		{
			reader.nextNull();
			return null;
		}

		return new Date( reader.nextLong() );
	}

	@Override
	public void write(
		JsonWriter writer,
		Date date )
		throws IOException {
		// TODO Auto-generated method stub
		if ( date == null )
		{
			writer.nullValue();
			return;
		}

		writer.value( date.getTime() );
	}

}
