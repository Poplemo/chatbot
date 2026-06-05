import java.util.ArrayList;

public class Index {

    // un index est un vecteur d'EntreeIndex
    private class EntreeIndex {

        // une EntreeIndex associe un vecteur trié d'entiers (sorties) à une String (entree)
        private String entree;
        private ArrayList<Integer> sorties;

        public ArrayList<Integer> getSorties() {
            return sorties;
        }


        //constructeur
        public EntreeIndex(String entree) {
            this.entree = entree;
            sorties = new ArrayList<>();
        }


        public int rechercherSortie(Integer sortie) {
            //{}=>{recherche dichotomique de sortie dans sorties (triée dans l'ordre croissant)
            // résultat = l'indice de sortie dans sorties si trouvé, - l'indice d'insertion si non trouvé }
            // sortie est vide
            if (sorties.isEmpty()) {
                return -1;
            }
            // sortie dépasse la taille de sorties
            if (sorties.get(sorties.size() - 1) < sortie) {
                return -sorties.size();
            }

            int inf = 0;
            int sup = sorties.size() - 1;
            int m;

            while (inf < sup) {
                m = (inf + sup) / 2;
                if (sorties.get(m) >= sortie) {
                    sup = m;
                } else {
                    inf = m + 1;
                }
            }

            if (sorties.get(sup).equals(sortie)) {
                return sup;
            } else {
                return -sup;
            }
        }


        public void ajouterSortie(Integer sortie) {
            //{}=>{insère sortie à la bonne place dans sorties (triée dans l'ordre croissant)
            // remarque : utilise rechercherSortie de EntreeIndex }
            // sorties est vide
            if (sorties.isEmpty()) {
                // ajout de sortie au début
                sorties.add(0, sortie);
            } else {
                // récupération de la position de sortie
                int pos = rechercherSortie(sortie);
                // vérification position 0 & différent de sortie
                if (pos <= 0 && !sorties.get(0).equals(sortie)) {
                    sorties.add(-pos, sortie);
                }
            }
        }

        @Override
        public String toString() {
            return entree + "=>" + sorties;
        }
    }

    //Un vecteur d'EntreeIndex trié sur l'attribut entree (String) des EntreeIndex
    private ArrayList<EntreeIndex> table;

    //constructeur
    public Index() {
        table = new ArrayList<>();
    }


    public int rechercherEntree(String entree) {
        //{}=>  {recherche dichotomique de entree dans table (triée dans l'ordre lexicographique des attributs entree des EntreeIndex) }
        //résultat =  l'indice de entree dans table si trouvé et -l'indice d'insertion sinon }

        // table est vide
        if (table.isEmpty()) {
            return -1;
        }

        // entree dépasse la taille de table
        if (table.get(table.size() - 1).entree.compareTo(entree) < 0) {
            return -table.size() - 1;
        } else {
            int inf = 0;
            int sup = table.size() - 1;
            int m;
            while (inf < sup) {
                m = (inf + sup) / 2;
                if (table.get(m).entree.compareTo(entree) >= 0) {
                    sup = m;
                } else {
                    inf = m + 1;
                }
            }

            if (table.get(sup).entree.compareTo(entree) == 0) {
                return sup;
            } else {
                return -sup - 1;
            }
        }
    }


    public void ajouterSortieAEntree(String entree, Integer sortie) {
        // {}=>{ajoute l'entier sortie dans les sorties associées à l'entrée entree
        // si l'entrée entree n'existe pas elle est créée.
        // ne fait rien si sortie était déjà présente dans ses sorties.
        // remarque : utilise la fonction rechercherEntree de Index et la procedure ajouterSortie de EntreeIndex}
        // table est vide
        if (table.isEmpty()) {
            // ajout d'un nouvel index d'entree à la pos 0
            table.add(0, new EntreeIndex(entree));
            // ajout d'une nouvelle sortie à la pos 0
            table.get(0).ajouterSortie(sortie);
        } else {
            // récupération de l'indice de entree
            int indiceRec = rechercherEntree(entree);
            // vérification si entree existe
            //si elle n'existe pas
            if (indiceRec < 0) {
                // récupération indice correcte
                int indicePos = -(indiceRec + 1);

                // ajout nouvel index d'entree
                table.add(indicePos, new EntreeIndex(entree));
                // ajout d'une nouvelle sortie
                table.get(indicePos).ajouterSortie(sortie);


            } else {
                // toutes les sorties de entree
                ArrayList<Integer> sortiePos = rechercherSorties(entree);

                int i = 0;
                while (i < sortiePos.size() && sortiePos.get(i).compareTo(sortie) != 0) {
                    i++;
                }

                //si la sortie n'est pas présente
                if (i == sortiePos.size()) {
                    // ajout d'une nouvelle sortie
                    table.get(indiceRec).ajouterSortie(sortie);
                }
            }
        }
    }


    public ArrayList<Integer> rechercherSorties(String entree) {
        // {}=>{résultat = les sorties associées à l'entrée entree
        // si l'entrée entree n'existe pas, une ArrayList vide est retournée.
        // remarque : utilise la fonction rechercherEntree de Index}
        // table est vide
        if (table.isEmpty()) {
            // nouvelle arraylist vide
            return new ArrayList<Integer>();
        }

        // recherche de l'indice d'entree
        int indice = rechercherEntree(entree);

        // indice strictement positif?
        if (indice > 0) {
            // toutes les sorties à l'indice
            return table.get(indice).getSorties();
        }
        // vérification indice à 0 & entree différent de la valeur d'entree à cette indice
        else if (indice == 0 && table.get(0).entree.equals(entree)) {
            // toutes les sorties à l'indice 0
            return table.get(0).getSorties();
        } else {
            // nouvelle arraylist vide
            return new ArrayList<Integer>();
        }
    }

    public void afficher() {
        // {}=>{affiche la table de l'index}
        // parcours de toute la table
        for (int i = 0; i < table.size(); i++) {
            // toutes les valeurs à la position i de table
            System.out.println(this.table.get(i));
        }
    }


}
