package server;

import javax.security.cert.X509Certificate;

public class ActionFactory {
    public static Action makeCommand(String input, User patient, String recNbr, String textToAppend, X509Certificate cert, OscarsDatabase odb, User nurse) {
        System.out.println("CommandFactory: parsed action name = " + input);
        Action action = null;
        switch (input) {
            case "1":
                System.out.println("Action is create");
                action = new CreateAction(patient, nurse , odb);
            break;
                case "2":
                System.out.println("Action is append");
                action = new AppendToRecordAction(patient, recNbr, textToAppend, odb);
                break;
            case "3":
                System.out.println("Action is read specific record");
                action = new ReadAction(patient, recNbr, odb);
                break;
            case "4":
                System.out.println("Action is delete");
                action = new DeleteAction(patient, recNbr, odb);
                break;
            case "5":
                System.out.println("Action is list all");
                action = new ListAllRecordsAction(patient, odb);
                break;
                default:
                    break;
        }
        action.setCert(cert);
        return action;
    }

}