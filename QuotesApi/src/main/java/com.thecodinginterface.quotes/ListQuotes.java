package com.thecodinginterface.quotes;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

public class ListQuotes implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {

        var response = new APIGatewayProxyResponseEvent()
                .withHeaders(Map.of(
                        "Content-Type", "application/json",
                        "X-Custom-Header", "application/json"));

        var quotesRepo = new QuotesS3Repository(System.getenv("QUOTES_BUCKET"), context.getLogger());

        List<Quote> quotes = quotesRepo.list();

        var gson = new Gson();

        return response.withStatusCode(HttpURLConnection.HTTP_OK)
                .withBody(gson.toJson(quotes));

    }
}
