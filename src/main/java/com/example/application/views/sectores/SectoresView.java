package com.example.application.views.sectores;

import com.example.application.data.entity.Sector;
import com.example.application.data.form.SectorForm;
import com.example.application.data.service.SectorService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "sectores")//, layout = MainView.class)
@PageTitle("Sectores")
public class SectoresView extends Div implements BeforeEnterObserver {

    private Grid<Sector> grid = new Grid<>(Sector.class, false);
    private SectorForm sectorForm;      
    private SectorService sectorService;
    private Button buttonNuevoSector;

    public SectoresView(@Autowired SectorService sectorService) {    	
        this.sectorService = sectorService;
        addClassNames("sectores-view", "flex", "flex-col", "h-full");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);        
        createEditorLayout(splitLayout);
        
        this.buttonNuevoSector = new Button("Nuevo Sector", click -> addSector());
        
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("w-full flex-wrap bg-contrast-15 py-s px-l");
        buttonLayout.setSpacing(true);
        buttonLayout.add(buttonNuevoSector);
        add(buttonLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("nombre").setAutoWidth(true);
        grid.addColumn("abreviacion").setAutoWidth(true);
        grid.addColumn(Sector::getModulos).setHeader("Modulos").setSortable(true).setAutoWidth(true);
     // Or you can use an ordinary function to setup the component
        grid.addComponentColumn(item -> createRemoveButton( item))
                .setHeader("Actions");
        grid.setItems(query -> sectorService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            editSector(event.getValue());
        });
        closeEditor();
    }
    
    private void addSector() {
		editSector(new Sector());
	}
    
    private void saveSector(SectorForm.SaveEvent evt) {
    	System.out.print("AQUI --->>>>>>   "+evt.getSector().getNombre());
    	sectorService.save(evt.getSector());
    	closeEditor();
    	refreshGrid();
    }
    
    private void deleteSector(Sector sector) {
    	sectorService.delete(sector);
    	refreshGrid();
    }
    
    private void editSector(Sector sector) {    	
		if(sector == null) {
			closeEditor();
		}else {
			this.sectorForm.setSector(sector);
			this.sectorForm.setVisible(true);
		}
	}
    
    private void closeEditor() {
		this.sectorForm.setSector(null);
		this.sectorForm.setVisible(false);
	}
    
    private void createEditorLayout(SplitLayout splitLayout) {
    	this.sectorForm = new SectorForm();
        this.sectorForm.addListener(SectorForm.SaveEvent.class, this::saveSector);
    	this.sectorForm.addListener(SectorForm.CancelEvent.class, e -> closeEditor());
    	splitLayout.addToSecondary(this.sectorForm);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
    	
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

    private Button createRemoveButton(Sector item) {
        //@SuppressWarnings("unchecked")
        Button button = new Button("",VaadinIcon.TRASH.create(), clickEvent -> {
        	deleteSector(item);
        });
        
        button.addThemeVariants(ButtonVariant.LUMO_ERROR);
        return button;
    }
}
