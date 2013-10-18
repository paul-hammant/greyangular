package com.paulhammant.greyangular;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.xml.XmlNamespaceDictionary;
import com.google.api.client.xml.XmlObjectParser;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

public class StringResponse implements HttpRequestInitializer {
    public void initialize(HttpRequest request) {
        request.setParser(new XmlObjectParser(new XmlNamespaceDictionary()) {
            public <T> T parseAndClose(InputStream inputStream, Charset charset, Class<T> tClass) throws IOException {
                StringWriter writer = new StringWriter();
                IOUtils.copy(inputStream, writer, charset.name());
                return (T) writer.toString();
            }
        });
    }
}
