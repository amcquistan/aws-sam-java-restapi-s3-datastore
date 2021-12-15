package com.thecodinginterface.quotes;

import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Optional;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Handler for requests to Lambda function.
 *
 * POST
 *   author: string
 *   message: string
 *   sourceUrl: string
 */
public class CreateQuote implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

        var response = new APIGatewayProxyResponseEvent()
                .withHeaders(Map.of("Content-Type", "application/json",
                        "X-Custom-Header", "application/json"));

        var gson = new Gson();
        var quotesRepo = new QuotesS3Repository(System.getenv("QUOTES_BUCKET"), context.getLogger());

        try {
            Optional<Quote> result = quotesRepo.saveSave(gson.fromJson(input.getBody(), Quote.class));

            if (result.isEmpty()) {
                return response.withStatusCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            }
            return response.withStatusCode(HttpURLConnection.HTTP_CREATED)
                    .withBody(gson.toJson(result.get()));
        } catch(JsonSyntaxException e) {
            context.getLogger().log("Bad Input: " + input.getBody());
            return response.withStatusCode(HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }
}
