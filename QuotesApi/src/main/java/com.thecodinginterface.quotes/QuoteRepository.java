package com.thecodinginterface.quotes;

import java.util.List;
import java.util.Optional;

public interface QuoteRepository {

    Optional<Quote> saveSave(Quote quote);

    List<Quote> list();

    Optional<Quote> get(String id);

    boolean delete(String id);
}
