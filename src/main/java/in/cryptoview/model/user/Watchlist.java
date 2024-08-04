package in.cryptoview.model.user;

import java.util.ArrayList;
import java.util.List;

public class Watchlist {

	private List<Integer> selectedCryptos;

	public Watchlist() {
		this.selectedCryptos = new ArrayList<>();
	}

	public List<Integer> getSelectedCryptos() {	
		return selectedCryptos;
	}

	public void addToWatchlist(int cryptoIndex) {
		selectedCryptos.add(cryptoIndex);
	}

	public void removeSelectedCrypto(int index) {
		if (index >= 0 && index < selectedCryptos.size()) {
			selectedCryptos.remove(index);
		} else {
			System.out.println("Invalid index in wishlist.");
		}
	}

	public void setSelectedCryptos(List<Integer> wishlist) {
		this.selectedCryptos.addAll(wishlist);
	}
}
