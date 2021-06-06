package com.vg.market.fmp.query;

import java.util.List;

public class FMPQueryBuilder
{
    public final static String FMP_BASE_URL = "www.financialmodelingprep.com";

    private final String key;

    public FMPQueryBuilder(String key) {
        this.key = key;
    }

    public String get(ApiFunction function, List<String> params) {
        switch (params.size()) {
            case 0:
                return get(function);
            case 1:
                return get(function, params.get(0));
            case 2:
                return get(function, params.get(0), params.get(1));
            default:
                return "Invalid params!";
        }
    }

    private String get(ApiFunction function) {
        return "/api/v3/" + function.get()
                +"?apikey=" + key;
    }

    private String get(ApiFunction function, String symbol) {
        return "/api/v3/" + function.get()
                +"/" + symbol
                +"?apikey=" + key;
    }

    private String get(ApiFunction function, String symbol, String interval) {
        return  "/api/v3/" + function.get()
                //TODO parse the given interval and decide
                +"/5min"
                +"/" + symbol
                +"?apikey=" + key;
    }
}
