package server;

import javax.net.ServerSocketFactory;
import javax.net.ssl.*;
import javax.security.cert.X509Certificate;
import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.security.KeyStore;

public class Server implements Runnable {
    private ServerSocket serverSocket = null;
    private static int numConnectedClients = 0;
    private User loggedInUser;
    private PrintWriter out;
    private BufferedReader in;
    private User nurse;
    private OscarsDatabase odb;
    ActionFactory actionFactory = new ActionFactory();

    public Server(ServerSocket ss) throws IOException {
        odb = new OscarsDatabase();
        initiateDataBase();
        serverSocket = ss;
        newListener();
    }

    private void initiateDataBase() {
        boolean initiateRecords = true;
        User patient_1 = new User("patient", "");
        User patient_2 = new User("patient", "");
        User nurse_1 = new User("nurse", "1");
        User nurse_2 = new User("nurse", "2");
        User doctor_1 = new User("doctor", "1");
        User doctor_2 = new User("doctor", "2");
        User gov_1 = new User("gov", "*");
        odb.addUser(patient_1);
        odb.addUser(patient_2);
        odb.addUser(nurse_1);
        odb.addUser(nurse_2);
        odb.addUser(doctor_1);
        odb.addUser(doctor_2);
        odb.addUser(gov_1);

        if(initiateRecords) {
            Record record1 = new Record("1", doctor_1, nurse_1, patient_1);
            Record record2 = new Record("1", doctor_2, nurse_1, patient_1);
            Record record3 = new Record("2", doctor_2, nurse_2, patient_2);
            record1.addNote("This is a first note");
            record2.addNote("This is a first note");
            record3.addNote("This is a first note");
            odb.addRecord(record1);
            odb.addRecord(record2);
            odb.addRecord(record3);
        }
    }

    public void run() {
        try {
            SSLSocket socket = (SSLSocket) serverSocket.accept();
            newListener();
            SSLSession session = socket.getSession();
            X509Certificate cert = (X509Certificate) session.getPeerCertificateChain()[0];
            String subject = cert.getSubjectDN().getName().split("=")[1];
            String issuer = cert.getIssuerDN().getName();
            BigInteger serial = cert.getSerialNumber();
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            numConnectedClients++;
            System.out.println("client.client connected");
            System.out.println("client.client name (cert subject DN field): " + subject);
            System.out.println("Issuer: :\n" + issuer + "\n");
            System.out.println("Serial: \n" + serial + "\n");
            System.out.println(numConnectedClients + " concurrent connection(s)\n");

            boolean loggedIn = login(cert);
            while (!loggedIn) {
                loggedIn = login(cert);
            }
            User emp = null;
            for (User user : odb.getUsers()) {
                if (user.equalsName(loggedInUser) || user.equals(loggedInUser) || (user.getType().equals("gov") && loggedInUser.getType().equals("gov"))) {
                    emp = user;
                }
            }
            System.out.println("Logged in as user: " + emp.toString());
            String clientMsg = null;
            User patient = null;
            while ((clientMsg = in.readLine()) != null) {
                System.out.println("received '" + clientMsg + "' from client.client");
                String[] info = clientMsg.split(";");
                String[] paddedInfo = new String[4];
                for(int i = 0; i < 4; i++){
                    if (info.length - 1 < i){
                        paddedInfo[i] = "";
                    } else {
                        paddedInfo[i] = info[i];
                    }
                }
                patient = odb.getPatient(paddedInfo[1]);
                if (patient == null) {
                    out.println("No user found with id: " + paddedInfo[1]);
                } else {
                    Action actionToPerform = actionFactory.makeCommand(paddedInfo[0], patient, paddedInfo[2], paddedInfo[3], cert, odb, nurse);
                    String toSend = actionToPerform.execute(loggedInUser);
                    out.println(toSend);
                }
                System.out.println("Done \n");
            }
            in.close();
            out.close();
            socket.close();
            numConnectedClients--;
            System.out.println("client.client disconnected");
            System.out.println(numConnectedClients + " concurrent connection(s)\n");
        } catch (IOException e) {
            System.out.println("Client died: " + e.getMessage());
            e.printStackTrace();
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean login(X509Certificate cert) throws IOException {
        String[] tmp = cert.getSubjectDN().getName().split("_");
        User toCompareWith = new User(tmp[0].split("=")[1], tmp[1]);
        String input = null;
        while ((input = in.readLine()) != null) {
            String[] loginInput = input.split(" ");
            String username = loginInput[0];
            String password = loginInput[1];
            String type = username.split("_")[0];
            String division = username.split("_")[1];
            nurse = null;
            loggedInUser = new User(type, division);
            for (User tempUser : odb.getUsers()) {
                if (tempUser.getType().equals("nurse") && tempUser.getDivision().equals(division)) {
                    nurse = tempUser;
                }
            }
            if (toCompareWith.getType().equals(type) &&
                    toCompareWith.getPass().equals(password) &&
                    toCompareWith.getDivision().equals(division)) {
                if (type.equals("patient")) {
                    loggedInUser.setDivision("");
                }
                out.println("true");
                out.flush();
                return true;
            }
            out.println("false");
            out.flush();
        }
        return false;
    }

    private void newListener() {
        (new Thread(this)).start();
    } // calls run()

    public static void main(String args[]) {
        System.out.println("\nServer Started\n");
        int port = 9876;
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        String type = "TLS";
        try {
            ServerSocketFactory ssf = getServerSocketFactory(type);
            ServerSocket ss = ssf.createServerSocket(port);
            ((SSLServerSocket) ss).setNeedClientAuth(true); // enables client.client authentication
            new Server(ss);
        } catch (IOException e) {
            System.out.println("Unable to start Server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static ServerSocketFactory getServerSocketFactory(String type) {
        if (type.equals("TLS")) {
            SSLServerSocketFactory ssf = null;
            try { // set up key manager to perform server.server authentication
                SSLContext ctx = SSLContext.getInstance("TLS");
                KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
                TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
                KeyStore ks = KeyStore.getInstance("JKS");
                KeyStore ts = KeyStore.getInstance("JKS");
                char[] password = "password".toCharArray();

                ks.load(new FileInputStream("certFiles/serverkeystore"), password);  // keystore password (storepass)
                ts.load(new FileInputStream("certFiles/servertruststore"), password); // truststore password (storepass)
                kmf.init(ks, password); // certificate password (keypass)
                tmf.init(ts);  // possible to use keystore as truststore here
                ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
                ssf = ctx.getServerSocketFactory();
                return ssf;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return ServerSocketFactory.getDefault();
        }
        return null;
    }
}
