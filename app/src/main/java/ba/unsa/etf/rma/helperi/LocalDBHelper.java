package ba.unsa.etf.rma.helperi;

import java.util.ArrayList;
import java.util.ListIterator;

import ba.unsa.etf.rma.interfejsi.FirestoreStorable;
import ba.unsa.etf.rma.klase.Kviz;

public class LocalDBHelper {

    public static ArrayList<? extends FirestoreStorable> getEntriesToAdd(ArrayList<? extends FirestoreStorable> firestore, ArrayList<? extends FirestoreStorable> local) {
        ArrayList<? extends FirestoreStorable> entriesToAdd = new ArrayList<>(firestore);
        ListIterator<? extends FirestoreStorable> listIterator = entriesToAdd.listIterator();
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
        return entriesToAdd;
    }

    public static ArrayList<Kviz> getUpdatedEntries(ArrayList<Kviz> firestore, ArrayList<Kviz> local) {
        ArrayList<Kviz> updatedEntries = new ArrayList<>(firestore);
        ListIterator<Kviz> listIterator = updatedEntries.listIterator();
        while(listIterator.hasNext()) {
            boolean remove = true;
            Kviz next = listIterator.next();
            for(Kviz quiz : local) {
                if(next.getDocumentID().equals(quiz.getDocumentID()) && MiscHelper.compareForUpdate(next, quiz)) {
                    remove = false;
                    break;
                }
            }
            if(remove) listIterator.remove();
        }
        return updatedEntries;
    }
}
