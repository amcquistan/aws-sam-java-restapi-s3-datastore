package com.thecodinginterface.quotes;

import java.util.Objects;

public class Quote {

    private String id;
    private String author;
    private String message;
    private String sourceUrl;

    public Quote(String id, String author, String message, String sourceUrl) {
        this.id = id;
        this.author = author;
        this.message = message;
        this.sourceUrl = sourceUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quote quote = (Quote) o;
        return author.equals(quote.author) && message.equals(quote.message) && sourceUrl.equals(quote.sourceUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, message, sourceUrl);
    }
}
