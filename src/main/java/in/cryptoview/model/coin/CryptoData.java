package in.cryptoview.model.coin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

 
@JsonIgnoreProperties(ignoreUnknown = true)
public class CryptoData {

	private String name;
	private String priceUsd;
	private String marketCapUsd;
	private String volumeUsd24Hr;

	// Getters and setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPriceUsd() {
		return priceUsd;
	}

	public void setPriceUsd(String priceUsd) {
		this.priceUsd = priceUsd;
	}

	public String getMarketCapUsd() {
		return marketCapUsd;
	}

	public void setMarketCapUsd(String marketCapUsd) {
		this.marketCapUsd = marketCapUsd;
	}

	public String getVolumeUsd24Hr() {
		return volumeUsd24Hr;
	}

	public void setVolumeUsd24Hr(String volumeUsd24Hr) {
		this.volumeUsd24Hr = volumeUsd24Hr;
	}

}
