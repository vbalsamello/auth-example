package com.example.application.views.modulos;

import java.util.Optional;

import com.example.application.data.entity.Modulo;
import com.example.application.data.entity.Nivel;
import com.example.application.data.entity.Sector;
import com.example.application.data.form.SectorForm;
import com.example.application.data.service.ModuloService;
import com.example.application.data.service.SectorService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
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
import com.vaadin.flow.component.textfield.TextField;

@Route(value = "modulos")//, layout = MainView.class)
@PageTitle("Modulos")
public class ModulosView extends Div implements BeforeEnterObserver {

    private final String MODULO_ID = "moduloID";
    private final String MODULO_EDIT_ROUTE_TEMPLATE = "modulos/%d/edit";

    private Grid<Modulo> grid = new Grid<>(Modulo.class, false);

    private TextField nombre = new TextField("nombre");
    private TextField descripcion = new TextField("descripcion");
    private ComboBox<Sector> sector = new ComboBox("sector");
    private ComboBox<Nivel> nivel = new ComboBox("Nivel");
    private SectorForm sectorForm;
    private Dialog dialog;
    
    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");
    private Button addSector = new Button("",VaadinIcon.PLUS.create());

    private BeanValidationBinder<Modulo> binder;
    
    private Modulo modulo;

    private ModuloService moduloService;
    private SectorService sectorService;

    public ModulosView(@Autowired ModuloService moduloService,@Autowired SectorService sectorService) {
        this.moduloService = moduloService;
        this.sectorService = sectorService;
        addClassNames("modulos-view", "flex", "flex-col", "h-full");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("nombre").setAutoWidth(true);
        grid.addColumn("descripcion").setAutoWidth(true);
        grid.addColumn(Modulo::getSectorNombre).setHeader("Sector").setSortable(true).setAutoWidth(true);
        grid.addColumn("nivel").setAutoWidth(true);
        grid.addComponentColumn(item -> createRemoveButton(item)).setHeader("Actions");
        grid.setItems(query -> moduloService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
        	var modulo = event.getValue();
            nombre.setValue(modulo.getNombre());
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Modulo.class);
        //binder.bindInstanceFields(this);
        binder.forField(nombre).bind("nombre");
    	binder.forField(descripcion).bind("descripcion");
    	binder.forField(nivel).bind("nivel");
        
        
        this.sectorForm = new SectorForm();
        this.sectorForm.addListener(SectorForm.SaveEvent.class, this::saveSector);        
        this.sectorForm.addListener(SectorForm.CancelEvent.class, e -> dialog.close());
    	
        
      //NUEVO SECTOR -----  nuevo sector
        this.dialog = new Dialog();        
        this.dialog.add(sectorForm);
      
        //dialog.setWidth("400px");
        //dialog.setHeight("150px");
        
        addSector.addClickListener(e -> {
        	dialog.open();
        });
        
        
        
        //NUEVO SECTOR -----  nuevo sector
        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.modulo == null) {
                    this.modulo = new Modulo();
                }
                binder.writeBean(this.modulo);

                moduloService.update(this.modulo);
                clearForm();
                refreshGrid();
                Notification.show("Modulo details stored.");
                UI.getCurrent().navigate(ModulosView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the modulo details.");
            }
        });

    }
    
    private void saveSector(SectorForm.SaveEvent evt) {
    	sectorService.save(evt.getSector());
    	dialog.close();
    	sector.setItems(sectorService.findAll());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
    	nivel.setItems(Nivel.values());    	
    	sector.setItemLabelGenerator(Sector::getNombre);
    	sector.setItems(sectorService.findAll());
        Optional<Integer> moduloId = event.getRouteParameters().getInteger(MODULO_ID);
        if (moduloId.isPresent()) {
            Optional<Modulo> moduloFromBackend = moduloService.get(moduloId.get());
            if (moduloFromBackend.isPresent()) {
                populateForm(moduloFromBackend.get());
            } else {
                Notification.show(String.format("The requested modulo was not found, ID = %d", moduloId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(ModulosView.class);
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
        descripcion = new TextField("Descripcion");
        
        //combo y nuevo
        sector = new ComboBox("Sector");
        HorizontalLayout sectorLayout = new HorizontalLayout();
        sectorLayout.setAlignItems(Alignment.BASELINE);
        
        addSector.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        sectorLayout.add(sector,addSector);
        
        nivel = new ComboBox("Nivel");
        Component[] fields = new Component[]{
        		nombre
        		, descripcion
        		, sectorLayout
        		, nivel
        		};

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

    private void populateForm(Modulo value) {
        this.modulo = value;
        binder.readBean(this.modulo);

    }
    
    
    private void deleteModulo(Modulo modulo) {
    	moduloService.delete(modulo);
    	refreshGrid();
    }
    
    private void closeEditor() {
    	//sectorForm.setContact(null);
    	//sectorForm.setVisible(false);
        removeClassName("editing");
    }
    
    private Button createRemoveButton(Modulo item) {
        //@SuppressWarnings("unchecked")
        Button button = new Button("",VaadinIcon.TRASH.create(), clickEvent -> {
        	deleteModulo(item);
        });
        
        button.addThemeVariants(ButtonVariant.LUMO_ERROR);
        return button;
    }
}
