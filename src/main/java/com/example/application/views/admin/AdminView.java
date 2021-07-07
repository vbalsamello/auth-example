package com.example.application.views.admin;

import java.util.Optional;

import com.example.application.data.entity.Mascota;
import com.example.application.data.service.MascotaService;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.converter.StringToIntegerConverter;

@Route(value = "admin/:mascotaID?/:action?(edit)")//, layout = MainView.class)
@PageTitle("Admin")
public class AdminView extends Div implements BeforeEnterObserver {

    private final String MASCOTA_ID = "mascotaID";
    private final String MASCOTA_EDIT_ROUTE_TEMPLATE = "admin/%d/edit";

    private Grid<Mascota> grid = new Grid<>(Mascota.class, false);

    private TextField nombre;
    private TextField raza;
    private TextField edad;

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");

    private BeanValidationBinder<Mascota> binder;

    private Mascota mascota;

    private MascotaService mascotaService;

    public AdminView(@Autowired MascotaService mascotaService) {
        this.mascotaService = mascotaService;
        addClassNames("admin-view", "flex", "flex-col", "h-full");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("nombre").setAutoWidth(true);
        grid.addColumn("raza").setAutoWidth(true);
        grid.addColumn("edad").setAutoWidth(true);
        grid.setItems(query -> mascotaService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(MASCOTA_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(AdminView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Mascota.class);

        // Bind fields. This where you'd define e.g. validation rules
        //binder.forField(edad).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("edad");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.mascota == null) {
                    this.mascota = new Mascota();
                }
                binder.writeBean(this.mascota);

                mascotaService.update(this.mascota);
                clearForm();
                refreshGrid();
                Notification.show("Mascota details stored.");
                UI.getCurrent().navigate(AdminView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the mascota details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Integer> mascotaId = event.getRouteParameters().getInteger(MASCOTA_ID);
        if (mascotaId.isPresent()) {
            Optional<Mascota> mascotaFromBackend = mascotaService.get(mascotaId.get());
            if (mascotaFromBackend.isPresent()) {
                populateForm(mascotaFromBackend.get());
            } else {
                Notification.show(String.format("The requested mascota was not found, ID = %d", mascotaId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(AdminView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("flex flex-col");
        editorLayoutDiv.setWidth("400px");

        Div editorDiv = new Div();
        editorDiv.setClassName("p-l flex-grow");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        nombre = new TextField("Nombre");
        raza = new TextField("Raza");
        edad = new TextField("Edad");
        Component[] fields = new Component[]{nombre, raza, edad};

        for (Component field : fields) {
            ((HasStyle) field).addClassName("full-width");
        }
        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
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
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Mascota value) {
        this.mascota = value;
        binder.readBean(this.mascota);

    }
}
