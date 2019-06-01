package ba.unsa.etf.rma.helperi;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import ba.unsa.etf.rma.izuzeci.WrongParseException;
import ba.unsa.etf.rma.klase.Pitanje;

public class QuizParser {

     public static void validirajImeKviza(String ime, ArrayList<String> kvizoviIme) {
        for (String s : kvizoviIme) {
            if (s.equals(ime)) {
                throw new WrongParseException("Kviz kojeg importujete već postoji!");
            }
        }
    }

    public static Pitanje izdvojiPitanje(String[] s) {
        Pitanje p = new Pitanje();
        p.setNaziv(s[0]);
        p.setTekstPitanja(s[0]);
        ArrayList<String> odgovori = new ArrayList<>();
        int brojOdgovora = Integer.parseInt(s[1]);
        int indexTacnog = Integer.parseInt(s[2]);
        for (int i = 0; i < brojOdgovora; i++) odgovori.add(s[i + 3]);
        p.setOdgovori(odgovori);
        p.setTacan(odgovori.get(indexTacnog));
        return p;
    }

    public static boolean duplicateCheck(ArrayList<String> temp) {
        Set<String> duplicateCheck = new HashSet<>();
        for (String s : temp) {
            if (!duplicateCheck.add(s)) return false;
        }
        return true;
    }

    public static void checkLength(int length) {
         if(length != 3) {
             throw new WrongParseException("Datoteka kviza kojeg importujete nema ispravan format!");
         }
    }

    public static void checkNumbers(int num1, int num2, String msg) {
         if(num1 != num2)
             throw new WrongParseException(msg);
    }

    public static void checkAnswers(String[] quizData, ArrayList<String> temp) {
        for (int i = 1; i <= Integer.parseInt(quizData[2]); i++) {
            String[] questionData = temp.get(i).split(",");
            int brojOdogovora = Integer.parseInt(questionData[1]);
            checkNumbers(brojOdogovora + 3, questionData.length,"Kviz kojeg importujete ima neispravan broj odgovora!" );
            int index = Integer.parseInt(questionData[2]);
            checkIndex(index, brojOdogovora);
        }
    }

    public static void checkIndex(int index, int number) {
         if(index < 0 || index >= number)
             throw new WrongParseException("Kviz kojeg importujete ima neispravan index tačnog odgovora!");
    }

    public static void checkDuplicatesOnImport(String[] quizData, ArrayList<String> temp) {
        ArrayList<String> checkPitanje = new ArrayList<>();
        for (int i = 1; i <= Integer.parseInt(quizData[2]); i++) {
            Pitanje p = izdvojiPitanje(temp.get(i).split(","));
            if (duplicateCheck(p.getOdgovori()) == false)
                throw new WrongParseException("Kviz kojeg importujete nije ispravan postoji ponavljanje odgovora!");
            checkPitanje.add(p.getNaziv());
        }
        if (duplicateCheck(checkPitanje) == false)
            throw new WrongParseException("Kviz nije ispravan postoje dva pitanja sa istim nazivom!");
    }

    public static void checkLines(int num1, int num2, String msg) {
         if(num1 == num2)
             throw new WrongParseException(msg);
    }

    public static void doParse(ArrayList<String> temp, ArrayList<String> kvizoviIme) {
        QuizParser.checkLines(temp.size(), 0, "Datoteka kviza kojeg importujete nema ispravan format!");
        String[] quizData = temp.get(0).split(",");
        checkLength(quizData.length);
        QuizParser.validirajImeKviza(quizData[0], kvizoviIme);
        QuizParser.checkNumbers(Integer.parseInt(quizData[2]), temp.size() - 1, "Kviz kojeg importujete ima neispravan broj pitanja!");
        QuizParser.checkAnswers(quizData, temp);
        QuizParser.checkDuplicatesOnImport(quizData, temp);
    }

    public static void checkDuplicatesAfterImport(ArrayList<Pitanje> temp, ArrayList<Pitanje> added) {
         for(Pitanje t : temp) {
             for(Pitanje a : added) {
                 if(a.getNaziv().equals(t.getNaziv())) throw new WrongParseException("Pitanja iz datoteke su već dodana!");
             }
         }
    }
}
