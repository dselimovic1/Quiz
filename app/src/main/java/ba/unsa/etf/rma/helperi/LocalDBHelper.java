package ba.unsa.etf.rma.helperi;

import java.util.ArrayList;
import java.util.ListIterator;

import ba.unsa.etf.rma.interfejsi.FirestoreStorable;
import ba.unsa.etf.rma.klase.Kviz;
import ba.unsa.etf.rma.klase.Rang;

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
                    next.setID(local.get(i).getID());
                    listToUpdate.add(next);
                }
            }
        }
        return listToUpdate;
    }

    public static ArrayList<Rang> rangListsToAdd(ArrayList<Rang> listLocal, ArrayList<Rang> listFirestore) {
        ArrayList<Rang> entriesToAdd = new ArrayList<>();
        for(Rang r : listLocal) {
            boolean add = true;
            for(Rang f : listFirestore) {
                if(f.getImeKviza().equals(r.getImeKviza())) {
                    add = false;
                    break;
                }
            }
            if(add) entriesToAdd.add(r);
        }
        return entriesToAdd;
    }

    public static ArrayList<Rang> rangListsToUpdate(ArrayList<Rang> listLocal, ArrayList<Rang> listFirestore) {
        ArrayList<Rang> entriesToUpdate = new ArrayList<>();
        for(Rang r : listLocal) {
            for(Rang f : listFirestore) {
                if(r.getImeKviza().equals(f.getImeKviza()) == false) continue;
                if(r.getMapa().size() != f.getMapa().size()) entriesToUpdate.add(r);
            }
        }
        return entriesToUpdate;
    }

}
