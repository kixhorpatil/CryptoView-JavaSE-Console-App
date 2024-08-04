package in.cryptoview.model.coin;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CryptoDataList {

	//map JSON field name to Java property.
	@JsonProperty("data")
	private ArrayList<CryptoData> data;
	private long timestamp;

	public ArrayList<CryptoData> getData() {
		return data;
	}

	public void setData(ArrayList<CryptoData> data) {
		this.data = data;
	}

	 
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
