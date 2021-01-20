package com.littlegui.activemq.ssl;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * SSL Tool Class
 * Upload keystore and Certificate
 */
public class SSLUtils {

     /**
      * upload Certificate
      * @param trustStore
      * @return
      * @throws java.security.NoSuchAlgorithmException
      * @throws java.security.KeyStoreException
      * @throws java.io.IOException
      * @throws java.security.GeneralSecurityException
      */
     public static TrustManager[] loadTrustManager(String trustStore) throws java.security.NoSuchAlgorithmException, java.security.KeyStoreException,
                java.io.IOException, java.security.GeneralSecurityException {
           KeyStore ks = KeyStore. getInstance("JKS");
           ks.load( new FileInputStream(trustStore), null);
           TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory. getDefaultAlgorithm());
           tmf.init(ks);
           System. out.println( "init TrustManagers finish");
            return tmf.getTrustManagers();
     }

     /**
      * Upload keystore
      * @param keyStore
      * @param keyStorePassword
      * @return
      * @throws java.security.NoSuchAlgorithmException
      * @throws java.security.KeyStoreException
      * @throws java.security.GeneralSecurityException
      * @throws java.security.cert.CertificateException
      * @throws java.io.IOException
      * @throws java.security.UnrecoverableKeyException
      */
     public static KeyManager[] loadKeyManager(String keyStore, String keyStorePassword) throws java.security.NoSuchAlgorithmException,
                java.security.KeyStoreException, java.security.GeneralSecurityException, java.security.cert.CertificateException, java.io.IOException,
                java.security.UnrecoverableKeyException {
           KeyStore ks = KeyStore. getInstance("JKS");
           ks.load( new FileInputStream(keyStore), keyStorePassword.toCharArray());
           KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory. getDefaultAlgorithm());
           kmf.init(ks, keyStorePassword.toCharArray());
           System. out.println( "init KeyManager finish");
            return kmf.getKeyManagers();
     }
}