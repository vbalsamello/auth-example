package com.example.application.data.form;

import com.example.application.data.entity.Sector;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

public class SectorForm extends FormLayout {

	private TextField nombre = new TextField("Nombre");
	private TextField abreviacion = new TextField("Abreviatura");
	
	private Button save = new Button("Save");
	private Button cancel = new Button("Cancel");
	
	private BeanValidationBinder<Sector> binder;
	
	private Sector sector = new Sector();
	
	public SectorForm() {	
		
		Div editorLayoutDiv = new Div();
    	editorLayoutDiv.setClassName("flex flex-col");
        editorLayoutDiv.setWidth("400px");
        
    	createTitleLayout(editorLayoutDiv);

        Div editorDiv = new Div();
        editorDiv.setClassName("p-l flex-grow");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        nombre = new TextField("Nombre");
        nombre.setRequired(true);
        nombre.setErrorMessage("Nombre es un campo requerido");
        abreviacion = new TextField("Descripcion");
        abreviacion.setRequired(true);
        abreviacion.setErrorMessage("Descripción es un campo requerido");
        Component[] fields = new Component[]{nombre, abreviacion};

        for (Component field : fields) {
            ((HasStyle) field).addClassName("full-width");
        }
        formLayout.add(fields);
        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);
        
        //despues de definir todo con los campos de los formularios!
        binder = new BeanValidationBinder<>(Sector.class);
    	binder.bindInstanceFields(this);
    	
        add(editorLayoutDiv);        
	}
	
	private void createTitleLayout(Div editorLayoutDiv) {
		var titulo = new HorizontalLayout();
		titulo.setClassName("w-full flex-wrap bg-contrast-5 py-s px-l");
		titulo.setSpacing(true);
		titulo.add(new H3("Formulario Sector"));
		editorLayoutDiv.add(titulo);
	}
	
	private void createButtonLayout(Div editorLayoutDiv) {
		HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("w-full flex-wrap bg-contrast-15 py-s px-l");
        buttonLayout.setSpacing(true);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        buttonLayout.add(save, cancel);
        		
		//save.addClickShortcut(Key.ENTER);
		//cancel.addClickShortcut(Key.ESCAPE);
		
		save.addClickListener(click -> validateAndSave());
		cancel.addClickListener(click -> fireEvent(new CancelEvent(this)));
		editorLayoutDiv.add(buttonLayout);
	}


	private void validateAndSave() {
		try {
		    binder.writeBean(sector); 
		    fireEvent(new SaveEvent(this, sector)); 
		    
		  } catch (ValidationException e) {
		    e.printStackTrace();
		  }
	}
	
	public void setSector(Sector sector) {
		this.sector = sector;
		binder.readBean(sector);		
	}
	
	//EVENTOS
		public static abstract class SectorFormEvent extends ComponentEvent<SectorForm>{

			private Sector sector;
			
			public SectorFormEvent(SectorForm source, Sector sector) {
				super(source, false);
				this.sector = sector;
			}
			
			public Sector getSector() {
				return this.sector;
			}
		}
		
		public static class SaveEvent extends SectorFormEvent{
			SaveEvent(SectorForm source,Sector sector){
				super(source,sector);
			}
		}
		
		public static class DeleteEvent extends SectorFormEvent{
			DeleteEvent(SectorForm source,Sector sector){
				super(source,sector);
			}
		}
		
		public static class CancelEvent extends SectorFormEvent{
			CancelEvent(SectorForm source){
				super(source,null);
			}
		}
		
		
		public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			    ComponentEventListener<T> listener) { 
			  return getEventBus().addListener(eventType, listener);
			}
	
}
