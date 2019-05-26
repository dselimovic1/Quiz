package ba.unsa.etf.rma.interfejsi;

public interface FirestoreStorable {
    String getJSONFormat();
    void setDocumentID(String documentID);
    String getDocumentID();
}
