package ba.unsa.etf.rma.helperi;

import android.util.Log;

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
                if(firestoreStorable.getDocumentID() == null) continue;
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
        ArrayList<Kviz> listToUpdate = new ArrayList<>();
        for(Kviz next : firestore) {
            for(int i = 0; i < local.size(); i++) {
                if(next.getDocumentID().equals(local.get(i).getDocumentID()) && MiscHelper.compareForUpdate(next, local.get(i))) {
                    listToUpdate.add(next);
                }
            }
        }
        Log.d("LIST TO UPDATE", Integer.toString(listToUpdate.size()));
        return listToUpdate;
    }


}
