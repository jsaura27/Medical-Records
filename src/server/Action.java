package server;

import javax.security.cert.X509Certificate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public abstract class Action {
    protected String subject;
    protected String division;
    protected Logger log = Logger.getLogger("auditLog");
    protected FileHandler fh;

    public abstract String execute(User loggedInUser);

    public void setCert(X509Certificate cert) {
        String[] tmp = cert.getSubjectDN().getName().split("_");
        subject = tmp[0].split("=")[1];       //ex nurse_2 is a nurse in div 2
        division = tmp[1];

        try {
            fh = new FileHandler("logfile.log");
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
    }
}
