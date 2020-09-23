package de.cfschulze.getthesource.views.sources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.cfschulze.getthesource.data.entity.Person;
import de.cfschulze.getthesource.data.entity.Source;
import de.cfschulze.getthesource.data.service.PersonService;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.cfschulze.getthesource.data.service.SourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.artur.helpers.CrudServiceDataProvider;
import de.cfschulze.getthesource.views.main.MainView;
import com.vaadin.flow.router.RouteAlias;

@Route(value = "sources", layout = MainView.class)
@PageTitle("Sources")
@CssImport("./styles/views/sources/sources-view.css")
@RouteAlias(value = "", layout = MainView.class)
public class SourcesView extends Div {

    private Grid<Source> grid;

    private TextField url = new TextField();
    private TextField author = new TextField();
    private TextField title = new TextField();

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private HorizontalLayout searchLayout;

    private Binder<Source> binder;

    private Source source = new Source();

    private SourceService sourceService;

    public SourcesView(@Autowired SourceService sourceService) {
        setId("sources-view");
        this.sourceService = sourceService;


        // Configure Grid
        grid = new Grid<>(Source.class);
        grid.setColumns("url", "author", "title");
        grid.setDataProvider(new CrudServiceDataProvider<Source, Void>(sourceService));
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                Optional<Source> sourceFromBackend= sourceService.get(event.getValue().getId());
                // when a row is selected but the data is no longer available, refresh grid
                if(sourceFromBackend.isPresent()){
                    populateForm(sourceFromBackend.get());
                } else {
                    refreshGrid();
                }
            } else {
                clearForm();
            }
        });

        // Configure Form
        binder = new Binder<>(Source.class);

        // Bind fields. This where you'd define e.g. validation rules
        binder.bindInstanceFields(this);

        //configure Searchbar
        createSearchLayout(sourceService);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.source == null) {
                    this.source = new Source();
                }
                binder.writeBean(this.source);
                sourceService.update(this.source);
                clearForm();
                refreshGrid();
                Notification.show("Person details stored.");
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the person details.");
            }
        });

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);
    }

    private void createSearchLayout(SourceService sourceService){
        this.searchLayout = new HorizontalLayout();

        TextField textField = new TextField("URL source");
        textField.setPlaceholder("https://...");
        textField.addThemeName("bordered");


        // Button click listeners can be defined as lambda expressions

        Button buttonAddSource = new Button("add source",
                e -> {
                    try {
                        if (textField.getValue().trim().equals("")) {
                            Span content = new Span("Please enter a valid URL!");
                            Notification notification = new Notification(content);
                            notification.setDuration(3000);
                            notification.setPosition(Notification.Position.TOP_CENTER);
                            notification.open();


                        } else {

                            Source source_tmp = sourceService.search(textField.getValue());

                            source = new Source();
                            source.setTitle(source_tmp.getTitle());
                            source.setUrl(source_tmp.getUrl());


                            Notification.show("Hinzugefügt: " + source.getTitle());
                            binder.writeBean(source_tmp);
                            sourceService.update(source_tmp);
                            clearForm();
                            refreshGrid();

                            textField.clear();
                        }

                    } catch (IOException | ValidationException ioException) {
                        ioException.printStackTrace();
                    }
                });

        searchLayout.add(textField, buttonAddSource);
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setId("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setId("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        addFormItem(editorDiv, formLayout, url, "url");
        addFormItem(editorDiv, formLayout, author, "author");
        addFormItem(editorDiv, formLayout, title, "title");
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setId("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {

        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(searchLayout,grid);
    }

    private void addFormItem(Div wrapper, FormLayout formLayout, AbstractField field, String fieldName) {
        formLayout.addFormItem(field, fieldName);
        wrapper.add(formLayout);
        field.getElement().getClassList().add("full-width");
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Source value) {
        this.source = value;
        binder.readBean(this.source);
    }
}
