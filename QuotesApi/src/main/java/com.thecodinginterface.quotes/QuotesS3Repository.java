package com.thecodinginterface.quotes;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.gson.Gson;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuotesS3Repository implements QuoteRepository {

    private final String bucketName;
    private final S3Client s3Client;
    private final LambdaLogger logger;

    public QuotesS3Repository(String bucketName, LambdaLogger logger) {
        this.bucketName = bucketName;
        this.s3Client = S3Client.builder()
                .region(Region.of(System.getenv("AWS_REGION")))
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .build();
        this.logger = logger;
    }

    @Override
    public Optional<Quote> saveSave(Quote quote) {
        quote.setId(quoteToS3Key(quote));
        try {
            var request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(quote.getId())
                    .build();

            var gson = new Gson();
            var json = gson.toJson(quote);
            s3Client.putObject(request, RequestBody.fromString(json));

            return Optional.of(quote);
        } catch(Exception e) {
            logger.log("Failed to save quote");
            logger.log(e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Quote> list() {
        List<Quote> quotes = new ArrayList<>();

        try {
            var request = ListObjectsRequest.builder()
                    .bucket(bucketName)
                    .build();

            var response = s3Client.listObjects(request);

            List<S3Object> objects = response.contents();
            var itr = objects.listIterator();
            while(itr.hasNext()) {
                var s3Obj = (S3Object) itr.next();
                quotes.add(downloadFromKey(s3Obj.key()));
            }
        } catch(Exception e) {
            logger.log("Failed to list quotes");
            logger.log(e.getMessage());
        }
        return quotes;
    }

    @Override
    public Optional<Quote> get(String id) {
        try {
            return Optional.of(downloadFromKey(id));
        } catch(Exception e) {
            logger.log("Failed to fetch quote for id = " + id);
            logger.log(e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public boolean delete(String id) {
        try {
            var request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(id)
                    .build();

            s3Client.deleteObject(request);
            return true;
        } catch (Exception e) {
            logger.log("Failed to delete object with id = " + id);
            logger.log(e.getMessage());
            return false;
        }
    }


    // bucketname/author/quote-hashcode.json

    Quote downloadFromKey(String key) {
        var request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        ResponseBytes<GetObjectResponse> response = s3Client.getObject(request, ResponseTransformer.toBytes());

        var gson = new Gson();
        var quote = gson.fromJson(response.asString(StandardCharsets.UTF_8), Quote.class);
        return quote;
    }

    String authorNameToS3Prefix(Quote quote) {
        return quote.getAuthor().toLowerCase().replaceAll("\\s+", "-");
    }

    String quoteToS3Key(Quote quote) {
        var prefix = authorNameToS3Prefix(quote);
        var filename = String.format("%s.json", quote.hashCode());
        return String.format("%s/%s", prefix, filename);
    }
}
