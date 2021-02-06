package client;

import java.net.*;
import java.io.*;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;
import java.security.KeyStore;
import java.security.cert.*;
import java.math.BigInteger;
import java.util.Scanner;

/*
 * This example shows how to set up a key manager to perform client.client
 * authentication.
 *
 * This program assumes that the client.client is not inside a firewall.
 * The application can be modified to connect to a server.server outside
 * the firewall by following SSLSocketClientWithTunneling.java.
 */
public class client {



    public static void main(String[] args) throws Exception {
        String host = null, user = null;
        int port = 9876;
        for (int i = 0; i < args.length; i++) {
            System.out.println("args[" + i + "] = " + args[i]);
        }
        if (args.length < 2) {
            System.out.println("USAGE: java client.client host port");
            System.exit(-1);
        }

        try { /* get input parameters */
            host = args[0];
            port = Integer.parseInt(args[1]);
        	if(args.length < 3)
        		user = "doctor_1";	
        	else
        		user = args[2];
        } catch (IllegalArgumentException e) {
            System.out.println("USAGE: java client.client host port");
            System.exit(-1);
        }

        try { /* set up a key manager for client.client authentication */
            SSLSocketFactory factory = null;
            try {
                char[] password = "password".toCharArray();
                KeyStore ks = KeyStore.getInstance("JKS");
                KeyStore ts = KeyStore.getInstance("JKS");
                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                SSLContext ctx = SSLContext.getInstance("TLS");
                ks.load(new FileInputStream("certFiles/" + user + "_keystore"), password);  // keystore password (storepass)
				ts.load(new FileInputStream("certFiles/" + user + "_truststore"), password); // truststore password (storepass);
				kmf.init(ks, password); // user password (keypass)
				tmf.init(ts); // keystore can be used as truststore here
				ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
                factory = ctx.getSocketFactory();
            } catch (Exception e) {
                throw new IOException(e.getMessage());
            }
            SSLSocket socket = (SSLSocket)factory.createSocket(host, port);
            System.out.println("\nsocket before handshake:\n" + socket + "\n");

            /*
             * send http request
             *
             * See SSLSocketClient.java for more information about why
             * there is a forced handshake here when using PrintWriters.
             */
            socket.startHandshake();

            SSLSession session = socket.getSession();
            X509Certificate cert = (X509Certificate)session.getPeerCertificateChain()[0];
            String subject = cert.getSubjectDN().getName();
            String issuer = cert.getIssuerDN().getName();
            BigInteger serial = cert.getSerialNumber();
            System.out.println("certificate name (subject DN field) on certificate received from server.server:\n" + subject + "\n");
            System.out.println("Issuer: :\n" + issuer + "\n");
            System.out.println("Serial: \n" + serial + "\n");
            System.out.println("socket after handshake:\n" + socket + "\n");
            System.out.println("secure connection established\n\n");

            BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder sb;
            boolean loggedIn = false;
            while(!loggedIn) {
                System.out.print("Username: ");
                String username = read.readLine();
                System.out.print("Password: ");
                String password = read.readLine();
                out.println(username + " " + password);
                out.flush();
                String input = null;
                while ((input = in.readLine()) != null) {
                    loggedIn = input.equals("true") ? true : false;
                    break;
                }
            }

			for (;;) {
                sb = new StringBuilder();
                System.out.print(">");
                System.out.print(
                        "Enter the action you want to perform as a digit:" + "\n" +
                                "1: Create record for patient" + "\n" +
                                "2: Add note to record for patient " + "\n" +
                                "3: Read a record for a patient" + "\n" +
                                "4: Delete a record" + "\n" +
                                "5: List all records for a patient" + "\n");
                String input = read.readLine();
                sb.append(input);

                if(input.equalsIgnoreCase("quit")){
                    break;
                }

                System.out.print("Enter the id of the patient you wish to work with: ");
                String patientID = read.readLine();
                sb.append(";" + patientID);


                if (!(input.equals("5") || input.equals("1"))) {
                    System.out.print("Enter the record number: ");
                    String recordNumber = read.readLine();
                    sb.append(";" + recordNumber);
                }

                if (input.equals("2")) {
                    System.out.print("Text to append: ");
                    String textToAppend = read.readLine();
                    sb.append(";" + textToAppend);
                }

                out.println(sb.toString());
                out.flush();

                String returnString = null;
                while ((returnString = in.readLine()) != null) {
                    System.out.println(returnString);
                    break;
                }
            }
            in.close();
            out.close();
            read.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
