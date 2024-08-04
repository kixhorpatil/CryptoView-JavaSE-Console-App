package in.cryptoview.api.coincap;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.cryptoview.model.coin.CryptoData;
import in.cryptoview.model.coin.CryptoDataList;

public class CoinCapConnector {
    private static final CloseableHttpClient HTTP_CLIENT = HttpClients.createDefault();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String COINCAP_ASSETS_API_URL = "https://api.coincap.io/v2/assets?";
    private static final List<String> LIST_OF_CRYPTO = Arrays.asList(
        "bitcoin", "ethereum", "tether", "binance-coin", "solana", "usd-coin", "xrp", "dogecoin", "cardano",
        "tron", "avalanche", "wrapped-bitcoin", "shiba-inu", "chainlink", "polkadot", "bitcoin-cash", "near-protocol",
        "unus-sed-leo", "multi-collateral-dai", "litecoin", "polygon", "internet-computer", "uniswap", "ethereum-classic",
        "stellar", "monero", "stacks", "maker", "filecoin", "okb", "crypto-com-coin", "render-token", "vechain",
        "arweave", "the-graph", "injective-protocol", "thorchain", "cosmos", "theta", "lido-dao", "aave", "fantom",
        "algorand", "hedera-hashgraph", "flow", "kucoin-token", "quant", "eos", "bitcoin-sv", "axie-infinity",
        "elrond-egld", "neo", "helium", "akash-network", "gala", "tezos", "the-sandbox", "gatetoken", "nexo",
        "ecash", "mantra-dao", "conflux-network", "decentraland", "chiliz", "raydium", "gnosis-gno", "pendle",
        "oasis-network", "mina", "iota", "aioz-network", "dexe", "klaytn", "nervos-network", "trueusd", "livepeer",
        "zcash", "nxm", "1inch", "ftx-token", "pancakeswap", "theta-fuel", "kava", "iotex", "wemix", "fei-protocol",
        "compound", "trust-wallet-token", "synthetix-network-token", "xinfin-network", "wootrade", "aragon",
        "superfarm", "golem-network-tokens", "kusama", "dash", "curve-dao-token", "zilliqa", "illuvium"
    );

    static boolean isValidCrypto(String input) {
        return LIST_OF_CRYPTO.contains(input);
    }

    public List<CryptoData> getAllCryptoData() {
        List<CryptoData> allCryptoData = new ArrayList<>();
        for (String crypto : LIST_OF_CRYPTO) {
            CloseableHttpResponse response = null;
            try {
                URI uri = initCoinCapAssetApi(crypto);
                HttpGet request = new HttpGet(uri);
                response = HTTP_CLIENT.execute(request);
                String responseBody = EntityUtils.toString(response.getEntity());
                CryptoData cryptoData = parseCryptoDataFromJson(responseBody);
                if (cryptoData != null) {
                    allCryptoData.add(cryptoData);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (response != null) {
                    try {
                        response.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return allCryptoData;
    }

    public void sortCryptoDataByMarketCap(List<CryptoData> cryptoList) {
        if (cryptoList != null && !cryptoList.isEmpty()) {
            Collections.sort(cryptoList, new Comparator<CryptoData>() {
                @Override
                public int compare(CryptoData o1, CryptoData o2) {
                    Double marketCapO1 = safeParseDouble(o1.getMarketCapUsd());
                    Double marketCapO2 = safeParseDouble(o2.getMarketCapUsd());
                    return marketCapO2.compareTo(marketCapO1);
                }
            });
        }
    }

    private Double safeParseDouble(String value) {
        try {
            return value != null ? Double.parseDouble(value) : 0.0;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }


    private static URI initCoinCapAssetApi(String crypto) throws URISyntaxException {
        String searchQueryParam = "search=" + crypto;
        String limitQueryParam = "limit=1";
        String queryParam = String.join("&", Arrays.asList(searchQueryParam, limitQueryParam));
        return new URI(COINCAP_ASSETS_API_URL + queryParam);
    }

    private static CryptoData parseCryptoDataFromJson(String jsonData) throws JsonProcessingException {
        CryptoDataList cryptoDataList = OBJECT_MAPPER.readValue(jsonData, CryptoDataList.class);
        if (cryptoDataList != null && cryptoDataList.getData() != null && !cryptoDataList.getData().isEmpty()) {
            return cryptoDataList.getData().get(0);
        }
        return null;
    }
}
