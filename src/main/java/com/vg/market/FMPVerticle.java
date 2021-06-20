package com.vg.market;

import com.vg.market.fmp.query.ApiFunction;
import com.vg.market.fmp.query.FMPQueryBuilder;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class FMPVerticle extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger("SampleLogger");

    private FMPQueryBuilder queryBuilder;

    private HttpServer httpServer;

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new FMPVerticle());
    }

    @Override
    public void start() {
        WebClient client = WebClient.create(vertx);
        final Router router = Router.router(vertx);

        ConfigRetriever retriever = ConfigRetriever.create(vertx);
        retriever.getConfig(ar -> {
            if (ar.failed()) {
                // Failed to retrieve the configuration
            } else {
                JsonObject config = ar.result();
                queryBuilder = new FMPQueryBuilder(config.getString("apikey"));
            }
        });

        ApiFunction.getMap().forEach((k, v)->{
            router.route("/raw" + k).handler(ctx -> {

                List<String> params = new ArrayList<>();
                String symbol;
                switch (v.getParam()) {
                    case NO_PARAM:
                        break;
                    case SYMBOL_AND_INTERVAL:
                        symbol = ctx.request().getParam("symbol");
                        symbol = (symbol == null) ? "TSLA" : symbol;
                        String interval = ctx.request().getParam("interval");
                        interval = (interval == null) ? "5min" : interval;
                        params.add(0, symbol);
                        params.add(1, interval);
                        break;
                    case SYMBOL:
                        symbol = ctx.request().getParam("symbol");
                        symbol = (symbol == null) ? "TSLA" : symbol;
                        params.add(0, symbol);
                        break;
                }

                client
                        .get(443, FMPQueryBuilder.FMP_BASE_URL, queryBuilder.get(v, params))
                        .ssl(true)
                        .send()
                        .onSuccess(response -> {
                            log.debug("Received response with status code" + response.statusCode());
                            ctx.response().putHeader("content-type", "text/json")
                                    .send(response.body());
                        })
                        .onFailure(err ->
                                System.out.println("Something went wrong " + err.getMessage()));
            });
        });

        router.route("/alive").handler(ctx -> {
            System.out.println("Hello");
            ctx.response().putHeader("content-type", "text/plain")
                    .send("Hello");
        });

        httpServer = vertx.createHttpServer();
        httpServer.requestHandler(router).listen(9092).onFailure(
                h -> System.out.println("HTTP server failed on port 9092"));
    }

    @Override
    public void stop() throws Exception
    {
        super.stop();
        httpServer.close();
    }
}
