import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Thesaurus {

    private class EntreeSortie implements Comparable<EntreeSortie> {
        private String entree; // un mot
        private String sortie; // sa forme canonique

        public EntreeSortie(String entree, String sortie) {
            this.entree = entree;
            this.sortie = sortie;
        }

        public int compareTo(EntreeSortie o) {
            return this.entree.compareTo(o.entree);
        }
    }

    private ArrayList<EntreeSortie> table;

    public Thesaurus(String nomFichier) {
        //{}=>{ constructeur créant et initialisant l'attribut table à partir du contenu du fichier dont le nom est passé en paramètre, puis triant la table
        // en utilisant la méthode compareTo d'EntreeSortie
        // remarque 1 : utilise ajouterEntreeSortie et trierEntreesSorties
        // remarque 2 : pour la lecture du fichier, inspirez-vous de lireMotsOutils de Utilitaire
        // remarque 3 : pour les traitements de la chaîne lue, utilisez les méthodes indexOf,substring de String
        table = new ArrayList<>();
        try {
            FileInputStream file = new FileInputStream(nomFichier);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String ligne = scanner.nextLine();
                int indEntree = ligne.indexOf(":");
                String entree = ligne.substring(0, indEntree);
                String sortie = ligne.substring(indEntree + 1);
                ajouterEntreeSortie(entree, sortie);
            }

            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        trierEntreesSorties(table);
    }

    public void ajouterEntreeSortie(String entree, String sortie) {
        //{}=>{ajoute à la fin de la table une nouvelle EntreeSortie avec les attributs entree et sortie}
        table.add(new EntreeSortie(entree, sortie));
    }


    public String rechercherSortiePourEntree(String entree) {
        // {l'attribut table du thesaurus est trié sur l'attribut entree des Entree-Sortie}=>
        // {résultat = la forme canonique associée à entree dans le thésaurus si l'entrée entree existe,
        // entree elle-même si elle n'existe pas. La recherche doit être dichotomique.
        // remarque : utilise compareTo de EntreeSortie }

        int inf = 0;
        int sup = table.size() - 1;
        int m;

        while (inf <= sup) {
            m = (inf + sup) / 2;
            if (table.get(m).entree.compareTo(entree) == 0) {
                return table.get(m).sortie;
            } else if (table.get(m).entree.compareTo(entree) < 0) {
                inf = m + 1;
            } else {
                sup = m - 1;
            }
        }

        // mot inexistant dans la table
        return entree;
    }

    static void trierEntreesSorties(ArrayList<EntreeSortie> v) {
        //{} => {trie v sur la base de la méthode compareTo de EntreeSortie}
        int i = 0;
        // invariant
        while (i < v.size() - 1) {
            // premier à traiter
            int indMin = i;
            // début recherche suivante
            int j = i + 1;
            // parcours complet
            while (j < v.size()) {
                // plus petit de v
                if (v.get(j).compareTo(v.get(indMin)) < 0) {
                    // mis à jour
                    indMin = j;
                }
                // avancer pour recherche de indmin
                j++;
            }
            // permutation
            if (indMin != i) {
                // récupération valeur à permuter
                EntreeSortie temp = v.get(i);
                // permutation 1
                v.set(i, v.get(indMin));
                // permutation 2
                v.set(indMin, temp);
            }
            i++;
        }
    }


}
