package alert;
import json.JSONConvertable;
import transaction.DatedTransaction;

public abstract class Alert implements JSONConvertable {

    public abstract void send(DatedTransaction transaction);

    public static Alert fromJSON(String type) {
        switch (type) {
            case "EMERGENCY":
                return new EmergencyAlert();
            case "MILD":
                return new MildAlert();
            default:
                return null;
        }
    }
}