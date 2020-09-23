package de.cfschulze.getthesource.data.service;

import de.cfschulze.getthesource.data.entity.Person;
import de.cfschulze.getthesource.data.entity.Source;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

@Service
public class SourceService extends CrudService<Source, Integer> {

    public Source search(String url) throws IOException {

        Source source = new Source();

        Document document = Jsoup.connect(url).get();

        source.setTitle(document.title());
        source.setUrl(url);

        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        Date date = new Date();
        source.setRetrievalDate(df.format(date));


        return source;
    }

    private SourceRepository repository;

    public SourceService(@Autowired SourceRepository repository) {
        this.repository = repository;
    }

    @Override
    protected SourceRepository getRepository() {
        return repository;
    }


}