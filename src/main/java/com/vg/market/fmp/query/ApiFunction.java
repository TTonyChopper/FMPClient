package com.vg.market.fmp.query;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ApiFunction
{
    Quote("/quote", "quote", FunctionParam.SYMBOL),
    Intraday("/intraday", "historical-chart", FunctionParam.SYMBOL_AND_INTERVAL),
    Gainers("/gainers", "gainers", FunctionParam.NO_PARAM),
    Losers("/losers", "losers", FunctionParam.NO_PARAM),
    Actives("/actives", "actives", FunctionParam.NO_PARAM);

    private final static Map<String, ApiFunction> fmpMap;
    static {
        fmpMap = Arrays.stream(ApiFunction.values()).collect(
                Collectors.toMap(ApiFunction::getUrl, Function.identity()));
    }

    public static Map<String, ApiFunction> getMap()
    {
        return fmpMap;
    }

    String url;
    String value;
    FunctionParam param;

    ApiFunction(String url, String value, FunctionParam param) {
        this.url = url;
        this.value = value;
        this.param = param;
    }

    public String getUrl() {
        return url;
    }

    public String get() {
        return value;
    }

    public FunctionParam getParam()
    {
        return param;
    }
}
