package model;
public class Grille {

	private Tuile tuile[][];

	/**
	 * 
	 * @param ligne
	 * @param colonne
	 */
	public Tuile getTuile(int ligne, int colonne) {
		return this.tuile[ligne][colonne];
	}

	public Grille getGrille() {
		// TODO - implement Grille.getGrille
		throw new UnsupportedOperationException();
	}

}