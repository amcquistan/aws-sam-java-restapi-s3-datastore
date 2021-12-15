package com.thecodinginterface.quotes;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.net.HttpURLConnection;
import java.util.Map;

public class DeleteQuote implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
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

        var success = quotesRepo.delete(id);

        if (!success) {
            return response.withStatusCode(HttpURLConnection.HTTP_NOT_FOUND);
        }
        return response.withStatusCode(HttpURLConnection.HTTP_NO_CONTENT);
    }
}
