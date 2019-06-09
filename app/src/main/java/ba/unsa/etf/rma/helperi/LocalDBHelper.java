package ba.unsa.etf.rma.helperi;

import java.util.ArrayList;
import java.util.ListIterator;

import ba.unsa.etf.rma.klase.Kviz;

public class LocalDBHelper {

    public static ArrayList<Kviz> getQuizzesToAdd(ArrayList<Kviz> firestore, ArrayList<Kviz> local) {
        ArrayList<Kviz> temp = new ArrayList<>(firestore);
        ListIterator<Kviz> listIterator = temp.listIterator();
        while (listIterator.hasNext()) {
            boolean remove = true;
            for(Kviz quiz : local) {
                if(listIterator.next().getDocumentID().equals(quiz.getDocumentID())) {
                    remove = false;
                    break;
                }
            }
            if(remove) listIterator.remove();
        }
        return temp;
    }

}
