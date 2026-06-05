import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Scanner;

public class Utilitaire {

    private static final int NBMOTS_FORME = 5; // nombre maximal de mots-outils pris en compte pour les formes dans l'étape 2

    static public ArrayList<String> lireMotsOutils(String nomFichier) {
        //{}=>{résultat = le vecteur des mots outils construit à partir du fichier nomFichier}
        ArrayList<String> motsOutils = new ArrayList<>();
        try {
            FileInputStream file = new FileInputStream(nomFichier);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine();
                motsOutils.add(ligne);
            }

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return motsOutils;
    }

    static public ArrayList<String> lireReponses(String nomFichier) {
        //{}=>{résultat = le vecteur des réponses construit à partir du fichier nomFichier}
        ArrayList<String> reponses = new ArrayList<>();

        try {
            FileInputStream file = new FileInputStream(nomFichier);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine();
                reponses.add(ligne);
            }

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reponses;
    }

    static public ArrayList<String> lireQuestionsReponses(String nomFichier) {
        //{}=>{résultat = le vecteur des questions/réponses construit à partir du fichier nomFichier}
        ArrayList<String> questionsReponses = new ArrayList<>();

        try {
            FileInputStream file = new FileInputStream(nomFichier);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine();
                questionsReponses.add(ligne);
            }

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questionsReponses;


    }

    public static void ecrireFichier(String nomFichier, String chaineAEcrire) {
        //{}=>{la chaîne  chaineAEcrire est écrite après saut de ligne à la suite du fichier nomFichier}
        // true = mode append ? écrit à la suite sans effacer ce qui existe
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomFichier, true))) {
            writer.newLine();
            writer.write(chaineAEcrire);// ajoute un retour à la ligne
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    static private ArrayList<String> decoupeEnMots(String contenu) {
        //{}=>{résultat = le vecteur des mots de la chaîne contenu après pré-traitements divers}
        String chaine = contenu.toLowerCase();
        chaine = chaine.replace('\n', ' ');
        chaine = chaine.replace('?', ' ');
        chaine = chaine.replace('-', ' ');
        chaine = chaine.replace('\'', ' ');
        chaine = chaine.replace('.', ' ');
        chaine = chaine.replace(',', ' ');
        chaine = chaine.replace(':', ' ');
        chaine = chaine.replace(';', ' ');
        chaine = chaine.replace('\'', ' ');
        chaine = chaine.replace('"', ' ');
        chaine = chaine.replace('', ' ');
        chaine = chaine.replace('', ' ');
        chaine = chaine.replace("'", " ");
        chaine = chaine.replace('(', ' ');
        chaine = chaine.replace(')', ' ');
        chaine = chaine.replace('«', ' ');
        chaine = chaine.replace('-', ' ');
        chaine = chaine.replace('’', ' ');


        String[] tabchaine = chaine.split(" ");
        ArrayList<String> resultat = new ArrayList<>();

        for (int i = 0; i < tabchaine.length; ++i) {
            if (!tabchaine[i].equals("")) {
                resultat.add(tabchaine[i]);
            }
        }

        return resultat;
    }


    static private boolean existeChaine(ArrayList<String> mots, String mot) {
        //{}=>  {recherche séquentielle de mot dans mots
        // résultat =  true si trouvé et false sinon }

        int i = 0;
        while (i < mots.size() && !mots.get(i).equals(mot)) {
            i++;
        }

        //trouvé => true
        //pas trouvé => false
        return i != mots.size();
    }


    static private boolean existeChaineDicho(ArrayList<String> lesChaines, String chaine) {
        //{lesChaines (triée dans l'ordre lexicographique)}=>  {recherche dichotomique de chaine dans lesChaines
        // résultat =  true si trouvé et false sinon }

        if (lesChaines.isEmpty()) {
            return false;
        }

        int inf = 0;
        int sup = lesChaines.size() - 1;
        int millieu;

        while (inf < sup) {
            millieu = (inf + sup) / 2;

            if (lesChaines.get(millieu).compareTo(chaine) >= 0) {
                sup = millieu;
            } else {
                inf = millieu + 1;
            }
        }
        return lesChaines.get(sup).compareTo(chaine) == 0;
    }

    static public boolean entierementInclus(ArrayList<String> mots, String question) {
        //{mots est trié dans l'ordre lexicographique}=>
        // résultat = true si tous les mots de questions sont dans mots, false sinon
        // remarque : utilise decoupeEnMots et existeChaineDicho}

        ArrayList<String> motsQuestion = decoupeEnMots(question);

        int i = 0;
        while (i < motsQuestion.size() && existeChaineDicho(mots, motsQuestion.get(i))) {
            i++;
        }

        return i == motsQuestion.size();
    }


    static private int rechercherChaine(ArrayList<String> lesChaines, String chaine) {
        // {}=>{résultat = l'indice de chaine dans lesChaines si trouvé et -1 sinon }

        int i = 0;
        while (i < lesChaines.size() && !lesChaines.get(i).equals(chaine)) {
            i++;
        }

        if (i >= lesChaines.size()) {
            return -1;
        }
        return i;
    }


    static public void integrerNouvelleQuestionReponse(String question,
                                                       String reponse,
                                                       ArrayList<String> formes,
                                                       Index indexFormes,
                                                       ArrayList<String> motsOutils,
                                                       Thesaurus unThesaurus) {
        //{la forme de reponse n'existe pas ou n'est pas associée à question dans indexFormes}=>{la forme de reponse est ajoutée à la fin de formes si elle n'y est pas déjà
        // et indexFormes est mis à jour pour tenir compte de cette nouvelle question-réponse
        // remarque 1 : utilise calculForme, rechercherChaine, decoupeEnMots, existeChaineDicho, ajouterSortieAEntree, rechercherSortiePourEntree
        // remarque 2 : seuls les NBMOTS_FORME premiers mots-outils de la question sont pris en compte}

        //  calcul forme rep
        String formeReponse = calculForme(reponse, motsOutils, unThesaurus);

        // recherche indice
        int indForme = rechercherChaine(formes, formeReponse);

        // existe pas, ajout & récupération nouvel indice
        if (indForme == -1) {
            formes.add(formeReponse);
            indForme = formes.size() - 1;
        }


        // découpe question
        ArrayList<String> motsQuestion = decoupeEnMots(question);
        int nbOutils = 0;
        String motActuel;
        String motCanonique;

        // parcours tous les mots de la question
        int i = 0;
        while (i < motsQuestion.size() && nbOutils < NBMOTS_FORME) {
            // récupération mot de la question à la pos i
            motActuel = motsQuestion.get(i);

            // vérification si c'est un nombre
            if (estUnNombre(motActuel)) {
                // attribution d'un "tag"
                motActuel = "NUM";
            }

            // vérification si mot outil ou NUM
            if (motActuel.equals("NUM") || existeChaineDicho(motsOutils, motActuel)) {


                // recherche forme canonique (pour clé index)
                motCanonique = unThesaurus.rechercherSortiePourEntree(motActuel);

                // Construction de la clé : (motposition)
                String entreeIndex = motCanonique + "" + i;

                // association clé à l'indice de la forme de réponse trouvé précedemment
                indexFormes.ajouterSortieAEntree(entreeIndex, indForme);

                nbOutils++;
            }
            i++;
        }
    }


    static public void IntegrerNouvelleReponse(String reponse, ArrayList<String> reponses, Index indexContenu, ArrayList<String> motsOutils, Thesaurus unThesaurus) {
        //{reponse n'est pas présent dans reponses}=>{reponse est ajoutée à la fin de reponses et indexContenu est mis à jour pour tenir compte de cette nouvelle réponse
        // remarque : utilise decoupeEnMots, existeChaineDicho, ajouterSortieAEntree, rechercherSortiePourEntree

        // ajout de reponse à la liste
        reponses.add(reponse);
        // récupération indice
        int indiceNouvelleRep = reponses.size() - 1;

        // séparation en mots
        ArrayList<String> motsNouvelleReponse = decoupeEnMots(reponse);
        String motCanonique;

        // parcours mot de la nouvelle rep
        for (int i = 0; i < motsNouvelleReponse.size(); i++) {

            // ignorer les mots outils
            if (!existeChaineDicho(motsOutils, motsNouvelleReponse.get(i))) {
                // mot pointe vers nouvelle rep
                indexContenu.ajouterSortieAEntree(motsNouvelleReponse.get(i), indiceNouvelleRep);

                // synonymes (constructionIndexReponses)
                motCanonique = unThesaurus.rechercherSortiePourEntree(motsNouvelleReponse.get(i));

                // mot a une forme canonique
                if (!(motCanonique.compareTo(motsNouvelleReponse.get(i)) == 0)) {
                    // recherche dans reps globales, celles contiennent le mot
                    for (int j = 0; j < reponses.size(); j++) {
                        // découpe rep 2 (pour vérif)
                        ArrayList<String> motsAutreRep = decoupeEnMots(reponses.get(j));

                        // vérification mot canonique dans rep 2
                        if (existeChaineDicho(motsAutreRep, motCanonique)) {
                            // synonyme doit aussi pointer vers rep 2
                            indexContenu.ajouterSortieAEntree(motsNouvelleReponse.get(i), j);
                        }
                    }
                }
            }
        }
    }

    static public Index constructionIndexReponses(ArrayList<String> reponses, ArrayList<String> motsOutils, Thesaurus unThesaurus) {
        //{}=>{résultat = un index dont les entrées sont les mots des réponses (reponses) absents de motsOutils.
        // et les sorties sont les indices (dans reponses) des réponses les contenant.
        // remarque : utilise existeChaineDicho, decoupeEnMots et ajouterSortieAEntree }

        Index index = new Index();
        ArrayList<String> phrase;
        ArrayList<String> phraseCanonique;
        ArrayList<Integer> indexMotsCanonique;

        String motCanonique;

        //Pour chaque phrase réponses possibles
        for (int i = 0; i < reponses.size(); i++) {
            phrase = decoupeEnMots(reponses.get(i)); //je le découpe

            //Pour chaque mot de phrase
            for (String mot : phrase) {
                //Si ce n'est PAS un mot outils
                if (!existeChaineDicho(motsOutils, mot)) {
                    motCanonique = unThesaurus.rechercherSortiePourEntree(mot);
                    index.ajouterSortieAEntree(motCanonique, i); //ajout du mot non-outils avec l'index associés
                }
            }
        }
        return index;
    }


    static void trierChaines(ArrayList<String> v) {
        //{}=>{v est trié dans l'ordre lexicographique }
        int j;
        boolean onAPermute = true;
        int i = 0;
        while (onAPermute) {
            j = v.size() - 1;
            onAPermute = false;
            while (j > i) {
                if (v.get(j).compareTo(v.get(j - 1)) < 0) {
                    String tmp = v.get(j);
                    v.set(j, v.get(j - 1));
                    v.set(j - 1, tmp);
                    onAPermute = true;
                }
                j--;
            }
            i++;
        }
    }


    static ArrayList<Integer> maxOccurences(ArrayList<Integer> v, int seuil) {
        //{v trié} => {résultat = vecteur des entiers dont le nombre d'occurences
        // est maximal et au moins égal au seuil. Si le nombre d'occurences maximal est inférieur au seuil , un vecteur vide est retourné.
        // Par exemple, si V est [3,4,5,5,5,6,6,8,8,8,12,16,16,20]
        // si seuil<=3 alors le résultat est [5,8].
        // si le seuil>3 alors le résultat est []}

        ArrayList<Integer> temp = new ArrayList<>();
        if (v.isEmpty()) {
            return temp;
        }

        ArrayList<Integer> valeurs = new ArrayList<>();
        ArrayList<Integer> nbOccurences = new ArrayList<>();
        int compteur = 1;

        for (int i = 1; i < v.size(); i++) {
            if (v.get(i - 1).equals(v.get(i))) {
                compteur++;
            } else {
                valeurs.add(v.get(i - 1));
                nbOccurences.add(compteur);
                compteur = 1;
            }
        }

        if (!v.get(v.size() - 2).equals(v.getLast())) {
            compteur = 1;
            valeurs.add(v.get(v.size() - 1));
            nbOccurences.add(compteur);
        } else {
            valeurs.add(v.get(v.size() - 1));
            nbOccurences.add(compteur);
        }


        int max = nbOccurences.get(0);
        for (int i = 1; i < nbOccurences.size(); i++) {
            if (nbOccurences.get(i) > max) {
                max = nbOccurences.get(i);
            }
        }

        if (max < seuil) {
            return new ArrayList<Integer>();
        }

        ArrayList<Integer> lesmax = new ArrayList<>();
        for (int i = 0; i < nbOccurences.size(); i++) {
            if (nbOccurences.get(i) == max) {
                lesmax.add(valeurs.get(i));
            }
        }

        return lesmax;
    }


    static ArrayList<Integer> fusion(ArrayList<Integer> v1, ArrayList<Integer> v2) {
        //{v1 et v2 triés}=>{résultat = vecteur trié fusionnant v1 et v2 sans supprimer les répétitions
        // par exemple si v1 est [4,8,8,10,25] et v2 est [5,8,9,25]
        // le résultat est [4,5,8,8,8,9,10,25,25]}

        ArrayList<Integer> v1_v2 = new ArrayList<>();
        int posV1 = 0;
        int posV2 = 0;

        while (posV1 < v1.size() && posV2 < v2.size()) {
            if (v1.get(posV1) < v2.get(posV2)) {
                v1_v2.add(v1.get(posV1));
                posV1++;
            } else {
                v1_v2.add(v2.get(posV2));
                posV2++;
            }
        }

        while (posV1 < v1.size()) {
            v1_v2.add(v1.get(posV1));
            posV1++;
        }

        while (posV2 < v2.size()) {
            v1_v2.add(v2.get(posV2));
            posV2++;
        }

        return v1_v2;
    }


    static String calculForme(String chaine, ArrayList<String> motsOutils, Thesaurus unThesaurus) {
        //{}=>{résultat = la concaténation des NBMOTS_FORME premiers mots-outils de chaine séparés par des blancs
        // remarque 1 : utilise decoupeMots et existeChaineDicho
        // remarque 2 : la limitation de la taille des formes permet d'accepter des réponses terminant par des précisions }

        ArrayList<String> mots = decoupeEnMots(chaine);
        String forme = "";
        String motCanonique;
        int compteur = 0;

        int i = 0;
        while (i < mots.size() && compteur < NBMOTS_FORME) {
            motCanonique = unThesaurus.rechercherSortiePourEntree(mots.get(i));

            // vérification si c'est un nombre
            if (estUnNombre(motCanonique)) {
                // attribution d'un "tag"
                motCanonique = "NUM";
            }

            // vérification si mot outil ou NUM
            if (motCanonique.compareTo("NUM") == 0 || existeChaineDicho(motsOutils, motCanonique)) {
                // concaténation avec espace
                forme += motCanonique + " ";
                compteur++;
            }
            i++;
        }
        return forme;
    }


    /*
    static String calculForme(String chaine, ArrayList<String> motsOutils, Thesaurus unThesaurus) {
        //{}=>{résultat = la concaténation des NBMOTS_FORME premiers mots-outils de chaine séparés par des blancs
        // remarque 1 : utilise decoupeMots et existeChaineDicho
        // remarque 2 : la limitation de la taille des formes permet d'accepter des réponses terminant par des précisions }

        ArrayList<String> mots = decoupeEnMots(chaine);
        String forme = "";
        String motCanonique;
        int compteur = 0;

        // TRANSFORMER EN WHILE sans double condition
        for (int i = 0; i < mots.size() && compteur < NBMOTS_FORME; i++) {
            //si c'est un mot outils
            if (existeChaineDicho(motsOutils, mots.get(i))) {
                motCanonique = unThesaurus.rechercherSortiePourEntree(mots.get(i));
                forme += motCanonique + " ";
                compteur++;
            }
        }
        return forme;
    }*/

    static public ArrayList<String> constructionTableFormes(ArrayList<String> reponses, ArrayList<String> motsOutils, Thesaurus unThesaurus) {
        //{}=>{résultat = le vecteur de toutes les formes de réponses dans reponses.
        // remarque : utilise calculForme et existeChaine }

        ArrayList<String> table = new ArrayList<>();
        ArrayList<String> motsDecoupe;

        //pour chaque reponses
        for (int i = 0; i < reponses.size(); i++) {
            String forme = calculForme(reponses.get(i), motsOutils, unThesaurus);

            if (!existeChaine(table, forme)) {
                table.add(forme);
            }
        }
        return table;
    }

    // A MODIFIER TOUS LES NOMS ETCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
    static public Index constructionIndexFormes(ArrayList<String> questionsReponses, ArrayList<String> formes, ArrayList<String> motsOutils, Thesaurus unThesaurus) {
        //{}=>{résultat = un index dont les entrées sont les "mots-outils positionnés" des questions (par exemple l'entrée pour un "Qui" en première position sera "qui_0")
        // et les sorties sont les indices (dans formes) des formes de réponses répondant aux questions contenant le mot-outil à cette position.
        // remarque 1 : utilise calculForme, rechercherChaine, decoupeEnMots, rechercherEntree, existeChaineDicho et ajouterSortieAEntree
        // remarque 2 : utilisez les méthodes indexOf et substring de String pour décomposer la question-réponse en question et réponse
        // remarque 3 : seuls les NBMOTS_FORME premiers mots-outils de la question sont pris en compte}

        Index unIndex = new Index(); //retour

        ArrayList<String> question = new ArrayList<>();
        ArrayList<String> reponse = new ArrayList<>();
        ArrayList<String> valeur;

        int sep;
        int indexPos;
        int nbMotsOutils;

        String formeReponse;

        //Pour chaque question
        for (int i = 0; i < questionsReponses.size(); i++) {
            //la list motsFormeQuestion est faite en prenant la partie question (gauche) et en enlevant les mots non_outils puis en le découpant

            // prépare la séparation
            sep = questionsReponses.get(i).indexOf("?");
            // sépare partie droite
            question.add(questionsReponses.get(i).substring(0, sep));
            // sépare partie gauche
            reponse.add(questionsReponses.get(i).substring(sep + 1));
        }

        for (int j = 0; j < question.size(); j++) {
            formeReponse = calculForme(reponse.get(j), motsOutils, unThesaurus);
            indexPos = rechercherChaine(formes, formeReponse);

            if (indexPos != -1) {
                nbMotsOutils = 0;
                valeur = decoupeEnMots(calculForme(question.get(j), motsOutils, unThesaurus));

                for (int k = 0; k < valeur.size() && nbMotsOutils < NBMOTS_FORME; k++) {
                    nbMotsOutils++;
                    unIndex.ajouterSortieAEntree(valeur.get(k) + "_" + k, indexPos);
                }
            }
        }
        return unIndex;
    }


    static public ArrayList<Integer> constructionReponsesCandidates(String question, Index IndexReponses, ArrayList<String> motsOutils, Thesaurus unThesaurus) {
        //{}=>{résultat = vecteur des identifiants de réponses contenant l'ensemble des mots non outils de la question.
        // remarque 1 : utilise decoupeEnMots, existeChaineDicho, rechercherSorties, fusion et maxOccurences
        // remarque 2 : maxOccurences est appelé en passant le nombre de mots non outils de la question comme valeur de seuil.
        // remarque 3 : on aurait pu calculer directement une intersection au lieu d'une fusion et se passer de maxOccurences mais on
        // souhaite pouvoir garder la possibilité d'assouplir par la suite la contrainte sur la présence de l'intégralité
        // des mots de la question dans la réponse }

        ArrayList<String> phrase = decoupeEnMots(question);

        ArrayList<String> temp = new ArrayList<>();
        ArrayList<Integer> sorties;
        int seuil = 0;

        String motCanonique;

        //pour chaque mot de la phrase
        for (int i = 0; i < phrase.size(); i++) {
            //si ce n'est pas un mot-outils
            //on l'ajoute à temp
            if (!existeChaineDicho(motsOutils, phrase.get(i))) {
                seuil++;
                motCanonique = unThesaurus.rechercherSortiePourEntree(phrase.get(i));
                temp.add(motCanonique);
            }
        }

        ArrayList<Integer> fusionT = new ArrayList<>();
        int i = 0;
        while (i < temp.size()) {
            sorties = IndexReponses.rechercherSorties(temp.get(i));
            fusionT = fusion(fusionT, sorties);
            i++;
        }
        return maxOccurences(fusionT, seuil);
    }


    static public boolean estUnNombre(String s) {
        //{s est non vide}=>{résultat = true si s ne contient que des caractères représentant des chiffres (>='0'&<='9') et false sinon}

        ArrayList<String> mots = decoupeEnMots(s);

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }


    static public ArrayList<Integer> selectionReponsesCandidates(String question,
                                                                 ArrayList<Integer> candidates,
                                                                 Index IndexFormes,
                                                                 ArrayList<String> reponses,
                                                                 ArrayList<String> formesReponses,
                                                                 ArrayList<String> motsOutils,
                                                                 Thesaurus unThesaurus) {
        //{}=>{résultat = vecteur des identifiants de réponses (parmi les candidates) dont la forme est cohérente
        // avec la question.
        // remarque 1 : utilise decoupeEnMots, existeChaineDicho, rechercherSorties, fusion, maxOccurences, calculForme
        // remarque 2 : l'algorithme procède en 2 temps. D'abord il trouve les formes de réponses qui répondent à la question.
        // puis ajoute au résultat l'identifiant des réponses candidates qui respectent au moins une de ces formes.
        // remarque 3 : pour trouver les formes de réponses qui répondent à la question, on utilise l'index des formes, et on sélectionne
        // en appelant maxOccurences (avec seuil = nombre des mots-outils de la question) celles associées dans l'index à tous les mots-outils de la question.
        // remarque 4 : seuls les NBMOTS_FORME premiers mots-outils de la question sont pris en compte}

        ArrayList<Integer> fusionFormes = new ArrayList<>();
        ArrayList<Integer> sorties;

        // découpe question
        ArrayList<String> motsQuestion = decoupeEnMots(question);

        int nbOutils = 0;
        String motActuel;
        String motCanonique;
        String entree;

        //---------------------------------
        //----------Verification-----------
        //---------------------------------

        // parcours de chaque mot de la question
        int i = 0;
        while (i < motsQuestion.size() && nbOutils < NBMOTS_FORME) {

            // récupération du mot de la question à la pos i
            motActuel = motsQuestion.get(i);

            // vérification si c'est un nombre
            if (estUnNombre(motActuel)) {
                // attribution d'un "tag"
                motActuel = "NUM";
            }

            // vérification si mot outil ou NUM
            if (motActuel.equals("NUM") || existeChaineDicho(motsOutils, motActuel)) {

                // synonyme
                motCanonique = unThesaurus.rechercherSortiePourEntree(motActuel);

                // construction clé : (mot_position)
                entree = motCanonique + "_" + nbOutils;

                // recherche dans l'index
                sorties = IndexFormes.rechercherSorties(entree);

                // fusion résultats
                fusionFormes = fusion(fusionFormes, sorties);

                nbOutils++;
            }
            i++;
        }

        if (fusionFormes.isEmpty()) {
            return candidates;
        }

        // On filtre : il faut que la forme candidate contienne TOUS les mots-outils trouvés (nbOutils)
        ArrayList<Integer> formesCompatibles = maxOccurences(fusionFormes, nbOutils);

        if (formesCompatibles.isEmpty()) {
            return candidates;
        }

        //---------------------------------
        //-------------Ajout---------------
        //---------------------------------

        ArrayList<Integer> resultat = new ArrayList<>();

        for (int indRep : candidates) {

            // Note : On suppose ici que calculForme gère aussi le NUM et le Thésaurus
            // (comme demandé dans le snippet fourni)
            String formeRep = calculForme(reponses.get(indRep), motsOutils, unThesaurus);
            // Si votre calculForme accepte le thesaurus, utilisez : calculForme(reponses.get(indRep), motsOutils, unThesaurus);

            int indForme = rechercherChaine(formesReponses, formeRep);

            if (indForme >= 0 && formesCompatibles.contains(indForme)) {
                resultat.add(indRep);
            }
        }

        return resultat;
    }

    static public boolean reponseExiste(String reponse,
                                        Index indexReponses,
                                        ArrayList<String> reponses,
                                        ArrayList<String> motsOutils) {
        //{}=>{résultat = true si la reponse est présente dans reponses et false sinon.
        // remarque 1 : utilise decoupeEnMots, rechercherSortiePourEntree, existeChaineDicho, rechercherSorties, fusion, maxOccurences
        // remarque 2 : Le vecteur reponses n'est pas trié. Afin d'éviter le coûteux parcours séquentiel du
        // vecteur, on utilise indexReponses pour trouver les réponses contenant tous les mots non outils de la
        // reponse, puis on vérifie si l'une d'entre elle est identique à reponse.}
        // separation mots
        ArrayList<String> mots = decoupeEnMots(reponse);
        ArrayList<String> motsNonOutils = new ArrayList<>();

        // Partie1
        int i = 0;
        // identification mots non outils
        while (i < mots.size()) {
            // mots non outils
            if (!existeChaineDicho(motsOutils, mots.get(i))) {
                // sauvegarde du mot
                motsNonOutils.add(mots.get(i));
            }
            i++;
        }

        // Partie2
        ArrayList<Integer> fusionT = new ArrayList<>();
        i = 0;
        // parcours de tous les mots sauvegardes
        while (i < motsNonOutils.size()) {
            // récupération des indexs qui contiennet le mot
            ArrayList<Integer> sorties = indexReponses.rechercherSorties(motsNonOutils.get(i));
            // accumulation
            fusionT = fusion(fusionT, sorties);
            i++;
        }

        // defintion seuil par nb de mots outils
        int seuil = motsNonOutils.size();
        // nb d'occurences
        ArrayList<Integer> candidats = maxOccurences(fusionT, seuil);

        // Partie3
        boolean trouve = false;
        int j = 0;
        // parcours de tous les mots & si pas trouvé
        while (j < candidats.size() && !trouve) {
            // récupération index du candidat
            int indexCandidat = candidats.get(j);
            // récupération phrase du candidat
            String reponseCandidat = reponses.get(indexCandidat);

            // vérification égalité réponse
            if (reponseCandidat.equalsIgnoreCase(reponse)) {
                trouve = true;
            }
            j++;
        }
        return trouve;
    }


    static public boolean formeQuestionReponseExiste(String question,
                                                     String reponse,
                                                     Index indexFormes,
                                                     ArrayList<String> formesReponses,
                                                     ArrayList<String> motsOutils,
                                                     Thesaurus unThesaurus) {
        //{}=>{résultat = * true si la forme de reponse est présente dans formesReponses
        // et qu'elle est accessible à partir des mots de la question en utilisant indexFormes.
        //                * false sinon.
        // remarque 1 : utilise decoupeEnMots, rechercherSortiePourEntree, existeChaineDicho, rechercherSorties, fusion, maxOccurences, calculForme
        // remarque 2 : Le vecteur formesReponses n'est pas trié. Afin d'éviter le coûteux parcours séquentiel du
        // vecteur, et afin de vérifier l'accessibilité à partir des mots de la question en utilisant indexFormes,
        // on utilise indexFormes pour trouver les formes indexées par les mots-outils de la
        // question, puis on vérifie si l'une de ces formes est identique à la forme de reponse.
        // remarque 3 : seuls les NBMOTS_FORME premiers mots-outils de question sont pris en compte}

        // découpe de la question en mots
        ArrayList<String> motsQuestion = decoupeEnMots(question);
        ArrayList<Integer> fusionFormes = new ArrayList<>();
        int nbOutils = 0;

        int i = 0;
        // parcours mots de la question (limite nbmots_forme sur nboutils)
        while (i < motsQuestion.size() && nbOutils < NBMOTS_FORME) {
            // récupération mot à la pos i
            String mot = motsQuestion.get(i);

            // vérification mot outil
            if (existeChaineDicho(motsOutils, mot)) {
                // création clé (mot + pos)
                String entree = mot + "_" + i;
                // récupération forme du mot
                ArrayList<Integer> sorties = indexFormes.rechercherSorties(entree);

                // accumulation resultats
                fusionFormes = fusion(fusionFormes, sorties);
                nbOutils++;
            }
            i++;
        }

        // vérification aucun mot outil trouvé
        if (nbOutils == 0 && fusionFormes.isEmpty()) {
            return false;
        }

        // formes avec tous les mots outils
        ArrayList<Integer> indicesCandidats = maxOccurences(fusionFormes, nbOutils);

        // aucune forme correspond à la question
        if (indicesCandidats.isEmpty()) {
            return false;
        }

        // calcul forme
        String formeRep = calculForme(reponse, motsOutils, unThesaurus);

        // vérification, formeRep est candidat
        for (int j = 0; j < indicesCandidats.get(j); j++) {
            // récupération formesReponses à la pos j
            String formeCandidate = formesReponses.get(j);

            // vérification correspondance
            if (formeCandidate.compareTo(formeRep) == 0) {
                return true;
            }
        }

        return false;
    }
}