package ba.unsa.etf.rma.interfejsi;

import org.json.JSONObject;

public interface FirestoreLoadable {

    FirestoreLoadable convertFromJSON(JSONObject json);
}
