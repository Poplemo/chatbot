import jdk.jshell.execution.Util;

import java.util.ArrayList;
import java.util.Scanner;

public class Chatbot {

    private static final String MESSAGE_IGNORANCE = "Je ne sais pas.";
    private static final String MESSAGE_APPRENTISSAGE = "Je vais te l'apprendre.";
    private static final String MESSAGE_BIENVENUE = "J'attends tes questions de culture générale.";
    private static final String MESSAGE_QUITTER = "Au revoir.";
    private static final String MESSAGE_INVITATION = "Je t'écoute.";
    private static final String MESSAGE_CONFIRMATION = "Très bien, c'est noté.";

    private static Index indexThemes; // index pour trouver rapidement les réponses à partir des mots NON outils de la question
    private static Index indexFormes; // index pour trouver rapidement les formes de réponse possibles à partir des mots-outils de la question

    static private ArrayList<String> motsOutils; // vecteur trié des mots outils
    static private ArrayList<String> reponses; // vecteur des réponses
    private static ArrayList<String> formesReponses; //vecteur des formes de réponses
    private static Thesaurus thesaurus; //thésaurus

    public static void main(String[] args) {

        // initialisation du vecteur des mots outils
        motsOutils = Utilitaire.lireMotsOutils("mots-outils.txt");
        // tri du vecteur des mots outils
        Utilitaire.trierChaines(motsOutils);

        // initialisation du vecteur des réponses
        reponses = Utilitaire.lireReponses("reponses.txt");
        //reponses = Utilitaire.lireReponses("mini_reponses.txt");

        // initialisation du thésaurus (partie 2)
        thesaurus = new Thesaurus("thesaurus.txt");
        // construction de l'index pour retrouver rapidement les réponses sur leurs thématiques
        indexThemes = Utilitaire.constructionIndexReponses(reponses, motsOutils, thesaurus);
        // COMMENTÉE POUR 5.3.e
        //indexThemes.afficher();

        // construction de la table des formes de réponses
        formesReponses = Utilitaire.constructionTableFormes(reponses, motsOutils, thesaurus);
        // COMMENTÉE POUR 5.3.e
        //System.out.println(formesReponses);

        // initialisation du vecteur des questions/réponses idéales
        ArrayList<String> questionsReponses = Utilitaire.lireQuestionsReponses("questions-reponses.txt");
        // ArrayList<String> questionsReponses = Utilitaire.lireQuestionsReponses("mini_questions-reponses.txt");
        // construction de l'index pour retrouver rapidement les formes possibles de réponses à partir des mots outils de la question
        indexFormes = Utilitaire.constructionIndexFormes(questionsReponses, formesReponses, motsOutils, thesaurus);
        // COMMENTÉE POUR 5.3.e
        //indexFormes.afficher();

        String reponse = "";
        String entreeUtilisateur = ""; // la dernière entrée de l'utilisateur
        String questionPrecedente = ""; // mémorise la derniere question


        Scanner lecteur = new Scanner(System.in);
        System.out.println();
        System.out.print("> ");
        System.out.println(MESSAGE_BIENVENUE);

        do { // on attend des questions
            System.out.print("> ");
            entreeUtilisateur = lecteur.nextLine();
            if (entreeUtilisateur.compareTo(MESSAGE_QUITTER) != 0) { //tant que l'utilisateur ne veut pas arrêter

                boolean uniquementMotsOutils = Utilitaire.entierementInclus(motsOutils, entreeUtilisateur);
                //System.out.println("[DEBUG] uniquement = " + uniquementMotsOutils);
                if (uniquementMotsOutils && !questionPrecedente.isEmpty()) {
                    //System.out.println("[DEBUG] blabla");
                    reponse = repondreEnContexte(entreeUtilisateur, questionPrecedente);
                } else {
                    reponse = repondre(entreeUtilisateur);
                    questionPrecedente = entreeUtilisateur;
                }
                System.out.println("> " + reponse);
            }
        } while (entreeUtilisateur.compareToIgnoreCase(MESSAGE_QUITTER) != 0);


    }


    static private String repondre(String question) {
        //System.out.println("[DEBUG] repondre");
        ArrayList<Integer> reponsesCandidates;
        // initialisation en construisant les reponses candidates
        reponsesCandidates = Utilitaire.constructionReponsesCandidates(question, indexThemes, motsOutils, thesaurus);
        // pas de reponses candidates?

        if (reponsesCandidates.isEmpty()) {
            // message jsp
            return MESSAGE_IGNORANCE;
        }
        ArrayList<Integer> reponsesSelectionnees;
        // initialisation en construisant de nouvelles reponses candidates à partir de reponsesCandidates
        reponsesSelectionnees = Utilitaire.selectionReponsesCandidates(question, reponsesCandidates, indexFormes, reponses, formesReponses, motsOutils, thesaurus);
        // pas de nouvelles reponses candidates
        if (reponsesSelectionnees.isEmpty()) {
            // message jsp
            return MESSAGE_IGNORANCE;
        }

        // 5.3.d
        //String resultat = "";
        // parcours de toutes les reponses possibles
        //for (int i = 0; i < reponsesSelectionnees.size(); i++) {
        //    // rechercher de l'indice de la reponse i
        //    int indice = reponsesSelectionnees.get(i);
        //    // récupération de la bonne reponse pour cette indice (reponse i)
        //    String textReponse = reponses.get(indice);
        //    // concaténation & accumulation des réponses possibles
        //    resultat += textReponse + "\n";
        //}

        // 5.3.e
        // selection d'une valeur de question aléatoire possible par rapport aux reponses selectionnees
        int indexAleatoire = (int) (Math.random() * reponsesSelectionnees.size());
        System.out.println("[DEBUG] Index Rep aléatoire = " + indexAleatoire);
        // recuperation d'une reponse possible aléatoire avec indexAleatoire
        int numReponses = reponsesSelectionnees.get(indexAleatoire);
        System.out.println("[DEBUG] Num rep aleatoire = " + numReponses);

        // une reponse aléatoire possible
        return reponses.get(numReponses);
    }


    // partie 2
    static private String repondreEnContexte(String question, String questionPrecedente) {
        //System.out.println("[DEBUG] repondreEnContext");

        ArrayList<Integer> reponsesCandidates;
        // initialisation en construisant les reponses candidates à partir de la question précédente
        reponsesCandidates = Utilitaire.constructionReponsesCandidates(questionPrecedente, indexThemes, motsOutils, thesaurus);

        // pas de reponses candidates
        if (reponsesCandidates.isEmpty()) {
            // message jsp
            return MESSAGE_IGNORANCE;
        }

        ArrayList<Integer> reponsesSelectionnees;
        // initialisation en construisant de nouvelles reponses candidates à partir de reponsesCandidates
        reponsesSelectionnees = Utilitaire.selectionReponsesCandidates(question, reponsesCandidates, indexFormes, reponses, formesReponses, motsOutils, thesaurus);

        // pas de de reponses selectionnes
        if (reponsesSelectionnees.isEmpty()) {
            // message jsp
            return MESSAGE_IGNORANCE;
        }

        // selection d'une valeur de question aléatoire possible par rapport aux reponses selectionnees
        int indexAleatoire = (int) (Math.random() * reponsesSelectionnees.size());
        // recuperation d'une reponse possible aléatoire avec indexAleatoire
        int numReponses = reponsesSelectionnees.get(indexAleatoire);

        // une reponse aléatoire possible
        return reponses.get(numReponses);


    }


}