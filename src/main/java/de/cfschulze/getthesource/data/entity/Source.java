package de.cfschulze.getthesource.data.entity;

import de.cfschulze.getthesource.data.AbstractEntity;

import javax.persistence.Entity;

@Entity
public class Source extends AbstractEntity {

    private String title;
    private String author;
    private String publicationDate;
    private String retrievalDate;
    private String url;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }


    public String getRetrievalDate() {
        return retrievalDate;
    }

    public void setRetrievalDate(String retrievalDate) {
        this.retrievalDate = retrievalDate;
    }

    @Override
    public String toString() {
        return this.author + "\t" + this.publicationDate +
                "\t" + this.title + "\t" + this.url + "\t" + this.retrievalDate;
    }
}
