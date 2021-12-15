package com.thecodinginterface.quotes;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Optional;

public class GetQuote implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {

        var response = new APIGatewayProxyResponseEvent()
                .withHeaders(Map.of(
                        "Content-Type", "application/json",
                        "X-Custom-Header", "application/json"));

        var quotesRepo = new QuotesS3Repository(System.getenv("QUOTES_BUCKET"), context.getLogger());

        var author = request.getPathParameters().get("author");
        var filename = request.getPathParameters().get("filename");
        var id = String.format("%s/%s", author, filename);

        Optional<Quote> result = quotesRepo.get(id);

        if (result.isEmpty()) {
            return response.withStatusCode(HttpURLConnection.HTTP_NOT_FOUND);
        }

        var gson = new Gson();
        return response.withStatusCode(HttpURLConnection.HTTP_OK)
                .withBody(gson.toJson(result.get()));
    }
}
