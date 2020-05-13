/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import org.springframework.stereotype.Service;

/**
 *
 * @author eduar
 */
@Service
public class IDPResponse {
    
    public String getResponseSOAP(String url) throws MalformedURLException, ProtocolException, IOException{
    StringBuffer response = null; 
        try{
            URL obj = new URL(url);
            
            HttpsURLConnection conn1;

                 conn1 = (HttpsURLConnection) obj.openConnection();

                conn1.setHostnameVerifier(new HostnameVerifier() {
                  @Override
                  public boolean verify(String hostname, SSLSession session) {
                    return true;
                  }
                });
            
            conn1.setRequestMethod("GET");
            conn1.setRequestProperty("Content-Type","application/soap+xml; charset=utf-8");
            conn1.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(conn1.getOutputStream())) {
                wr.flush();
            }
            String responseStatus = conn1.getResponseMessage();
            System.out.println(responseStatus);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(
                    conn1.getInputStream()))) {
                String inputLine;
                response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
            System.out.println("response:" + response.toString());

            } catch (IOException e) {
                System.out.println(e);
        }
                    return response.toString();
    }
    
}
