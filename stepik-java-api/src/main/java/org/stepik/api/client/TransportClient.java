package org.stepik.api.client;

import java.io.IOException;
import java.util.Map;

/**
 * @author meanmail
 */
public interface TransportClient {
   
    ClientResponse post( String url,  String body) throws IOException;

   
    ClientResponse get( String url) throws IOException;

   
    ClientResponse post( String url,  String body,  Map<String, String> headers) throws IOException;

   
    ClientResponse get( String url,  Map<String, String> headers) throws IOException;
}
