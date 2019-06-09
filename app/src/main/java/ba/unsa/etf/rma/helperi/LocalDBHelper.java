package ba.unsa.etf.rma.helperi;

import java.util.ArrayList;
import java.util.ListIterator;

import ba.unsa.etf.rma.interfejsi.FirestoreStorable;

public class LocalDBHelper {

    public static ArrayList<? extends FirestoreStorable> getEntriesToAdd(ArrayList<? extends FirestoreStorable> firestore, ArrayList<? extends FirestoreStorable> local) {
        ArrayList<? extends FirestoreStorable> temp = new ArrayList<>(firestore);
        ListIterator<? extends FirestoreStorable> listIterator = temp.listIterator();
        while (listIterator.hasNext()) {
            boolean remove = false;
            FirestoreStorable next = listIterator.next();
            for(FirestoreStorable firestoreStorable : local) {
                if(next.getDocumentID().equals(firestoreStorable.getDocumentID())) {
                    remove = true;
                    break;
                }
            }
            if(remove) listIterator.remove();
        }
        return temp;
    }

}
