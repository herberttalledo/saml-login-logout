/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.config;

/**
 *
 * @author eduar
 */
import com.ibm.cloud.objectstorage.ClientConfiguration;
import com.ibm.cloud.objectstorage.auth.AWSCredentials;
import com.ibm.cloud.objectstorage.auth.AWSStaticCredentialsProvider;
import com.ibm.cloud.objectstorage.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.ibm.cloud.objectstorage.oauth.BasicIBMOAuthCredentials;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3;
import com.ibm.cloud.objectstorage.services.s3.AmazonS3ClientBuilder;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigObjectStorage {

    private static final String COS_ENDPOINT = "s3.us-south.cloud-object-storage.appdomain.cloud";
    private static final String COS_API_KEY_ID = "43ESRLBy49ZysqT1GvNnELQxWvs4Ulrh7IjVFXO14XC0";
    private static final String COS_SERVICE_CRN = "crn:v1:bluemix:public:cloud-object-storage:global:a/a6ae94275b1b1a702dceb382e6f3f0ea:35b10bdc-46df-4e64-8034-d75706717c36::";
    private static final String COS_BUCKET_LOCATION = "";

    private static AmazonS3 cos;

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigObjectStorage.class);

    public InputStream getfile() {
        LOGGER.info("create client");
        cos = createClient(COS_API_KEY_ID, COS_SERVICE_CRN, COS_ENDPOINT, COS_BUCKET_LOCATION);

        LOGGER.info("return getObject");
        return cos.getObject("novum-files", "apiConnect_cert.jks").getObjectContent();
    }

    public static AmazonS3 createClient(String api_key, String service_instance_id, String endpoint_url, String location) {
        AWSCredentials credentials = new BasicIBMOAuthCredentials(api_key, service_instance_id);
        ClientConfiguration clientConfig = new ClientConfiguration().withRequestTimeout(10000);
        clientConfig.setUseTcpKeepAlive(true);

        return AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
                        .withEndpointConfiguration(new EndpointConfiguration(endpoint_url, location))
                        .withPathStyleAccessEnabled(true).withClientConfiguration(clientConfig).build();
    }
}