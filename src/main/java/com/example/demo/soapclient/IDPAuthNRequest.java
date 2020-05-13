/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.soapclient;

import static com.example.demo.constants.Constants.MAIN_URL;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import org.joda.time.DateTime;

import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.common.Extensions;
import org.opensaml.saml2.common.impl.ExtensionsBuilder;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.impl.AuthnRequestBuilder;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.schema.XSAny;
import org.opensaml.xml.schema.impl.XSAnyBuilder;
import org.opensaml.xml.util.Base64;
import org.opensaml.xml.util.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;

/**
 *
 * @author eduar
 */
@Service
public class IDPAuthNRequest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(IDPAuthNRequest.class);
    
    public String getURL() throws IOException, ConfigurationException, UnmarshallingException, MarshallingException, MarshallingException{
        String parameterSigAlg = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
        
        //Generate ID
        String randId = generateUuid();
        LOGGER.info( "Random ID: {}", randId );  

        //Create an issuer Object
        IssuerBuilder issuerBuilder = new IssuerBuilder();
        Issuer issuer = issuerBuilder.buildObject("urn:oasis:names:tc:SAML:2.0:assertion", "Issuer", "saml2" );
        issuer.setValue("https://auth.pe-pre.baikalplatform.com/");

        DateTime issueInstant = new DateTime();
        AuthnRequestBuilder authRequestBuilder = new AuthnRequestBuilder();
        AuthnRequest authRequest = authRequestBuilder.buildObject("urn:oasis:names:tc:SAML:2.0:protocol", "AuthnRequest", "saml2p");
        authRequest.setForceAuthn(false);
        authRequest.setIsPassive(false);
        authRequest.setIssueInstant(issueInstant);
        authRequest.setProtocolBinding("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST");
        authRequest.setAssertionConsumerServiceURL("https://auth.pe-pre.baikalplatform.com/saml-SSO");
        authRequest.setDestination("https://identity-des.mimovistar.com.pe:24443/oamfed/idp/samlv20");
        authRequest.setExtensions( buildExtensions() );
        authRequest.setIssuer(issuer);
        authRequest.setID(randId);
        authRequest.setVersion( SAMLVersion.VERSION_20 );
        String stringRep = authRequest.toString();
        LOGGER.info("New AuthnRequestImpl: {}", stringRep);
        LOGGER.info("Assertion Consumer Service URL: {}", authRequest.getAssertionConsumerServiceURL());

        // Now we must build our representation to put into the html form to be submitted to the idp
        Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(authRequest);
        Element authDOM = marshaller.marshall(authRequest);
        StringWriter rspWrt = new StringWriter();
        XMLHelper.writeNode(authDOM, rspWrt);
        String messageXML = rspWrt.toString();
        //String samlRequestIDP = new String(Base64.encodeBytes(messageXML.getBytes(), Base64.DONT_BREAK_LINES));

        Deflater deflater = new Deflater(Deflater.DEFLATED, true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(byteArrayOutputStream, deflater)) {
            deflaterOutputStream.write(messageXML.getBytes());
        }
        
        String samlRequestIDP = Base64.encodeBytes(messageXML.getBytes(), Base64.DONT_BREAK_LINES);
        
//        String samlRequestIDP = Base64.encodeBytes(byteArrayOutputStream.toByteArray(), Base64.DONT_BREAK_LINES);
//        String outputString = new String(byteArrayOutputStream.toByteArray());
        //LOGGER.info("Compressed String: " + outputString);
        
//        samlRequestIDP = URLEncoder.encode(samlRequestIDP);

        LOGGER.info("Converted AuthRequest: {}", messageXML);
        LOGGER.info("samlRequestIDP: {}", samlRequestIDP);
        //messageXML = messageXML.replace("<", "&lt;");
        //messageXML = messageXML.replace(">", "&gt;");

        
        String url = MAIN_URL+"?SAMLRequest=" + samlRequestIDP + "&RelayState=" + MAIN_URL + "&SigAlg=" + parameterSigAlg;
        LOGGER.info("URL: {}", url);
        return url;

        //HTTPRedirectDeflateEncoder httpRedirectDeflateEncoder = new HTTPRedirectDeflateEncoder();
        //httpRedirectDeflateEncoder.encode((MessageContext) authDOM);

    }
    
    protected Extensions buildExtensions() {
        XSAny acrValues = new XSAnyBuilder().buildObject("http://www.v7security.com/schema/2015/04/request", "AcrValues", "req");
        acrValues.setTextContent("3");
        
        XSAny authorizationId = new XSAnyBuilder().buildObject("http://www.v7security.com/schema/2015/04/request", "AuthorizationId", "req");
        authorizationId.setTextContent("d5210aa2-c945-4c28-90ae-ed44f2ffdaaf");
        
        XSAny applicationName = new XSAnyBuilder().buildObject("http://www.v7security.com/schema/2015/04/request", "ApplicationName", "req");
        applicationName.setTextContent("novum-mytelco");
        
        Extensions extensions = new ExtensionsBuilder().buildObject();
        extensions.getUnknownXMLObjects().add(0, acrValues);
        extensions.getUnknownXMLObjects().add(1, authorizationId);
        extensions.getUnknownXMLObjects().add(2, applicationName);
        
        return extensions;
    }
    
    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }
    
}
