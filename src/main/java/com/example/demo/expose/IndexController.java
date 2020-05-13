package com.example.demo.expose;

import com.example.demo.soapclient.IDPAuthNRequest;
import com.example.demo.util.IDPResponse;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import org.apache.http.protocol.HTTP;
import org.opensaml.saml2.core.Response;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.UnmarshallingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IndexController {

    @Autowired
    IDPAuthNRequest authNRequest;
    
    @Autowired
    IDPResponse responseSOAP;
    
    @RequestMapping("/")
    public String index() {
        return "index";
    }
    
    @RequestMapping(value = "/loginSOAP", method=RequestMethod.GET, produces = "application/xml")
    public ResponseEntity loginSOAP() throws IOException, ConfigurationException, UnmarshallingException, MarshallingException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, UnrecoverableKeyException {
        return ResponseEntity.status(HttpStatus.OK).body(responseSOAP.getResponseSOAP(authNRequest.getURL()));
    }
    
}