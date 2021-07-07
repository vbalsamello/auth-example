package com.example.application.views.admin;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import com.example.application.data.entity.Mascota;
import com.example.application.data.entity.Role;
import com.example.application.data.entity.Usuario;
import com.example.application.data.service.MascotaService;
import com.example.application.data.service.UsuarioService;
import com.example.application.views.main.MainView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;

@Route(value="adminuser/:usuarioID?/:action?(edit)")//,layout = MainView.class)
@PageTitle("Admin usuario")
public class AdminUserView extends Div implements BeforeEnterObserver {

    private final String USUARIO_ID = "usuarioID";
    private final String USUARIO_EDIT_ROUTE_TEMPLATE = "adminuser/%d/edit";

    private Grid<Usuario> grid = new Grid<>(Usuario.class, false);

    private TextField username = new TextField("Username");	
	private ComboBox<Role> role = new ComboBox("Role");
	private Checkbox active = new Checkbox("Activated");
	private Usuario usuarioBase = new Usuario();

    private Button cancel = new Button("Cancel");
    private Button save = new Button("Save");
    private Button delete = new Button("Delete");

    private BeanValidationBinder<Usuario> binder;

    private Usuario usuario;

    private UsuarioService usuarioService;

    public AdminUserView(@Autowired UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
        addClassNames("adminUser-view", "flex", "flex-col", "h-full");
        
        // Create UI
        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("username").setAutoWidth(true);
        grid.addColumn("role").setAutoWidth(true);
        grid.addColumn("active").setAutoWidth(true);
        grid.setItems(query -> usuarioService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(USUARIO_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(AdminView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Usuario.class);

        // Bind fields. This where you'd define e.g. validation rules
        //binder.forField(edad).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("edad");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        delete.addClickListener(e -> {
            try {
                if (this.usuario == null) {
                    this.usuario = new Usuario();
                }
                binder.writeBean(this.usuario);

                usuarioService.delete(this.usuario);
                clearForm();
                refreshGrid();
                Notification.show("User fue eliminado.");
                UI.getCurrent().navigate(AdminUserView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the usuario details.");
            }
        });
        
        save.addClickListener(e -> {
            try {
                if (this.usuario == null) {
                    this.usuario = new Usuario();
                }
                binder.writeBean(this.usuario);

                usuarioService.update(this.usuario);
                clearForm();
                refreshGrid();
                Notification.show("User details stored.");
                UI.getCurrent().navigate(AdminUserView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the usuario details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
    	role.setItems(Role.values());
        Optional<Integer> usuarioId = event.getRouteParameters().getInteger(USUARIO_ID);
        if (usuarioId.isPresent()) {
            Optional<Usuario> usuarioFromBackend = usuarioService.get(usuarioId.get());
            if (usuarioFromBackend.isPresent()) {
                populateForm(usuarioFromBackend.get());
            } else {
                Notification.show(String.format("The requested usuario was not found, ID = %d", usuarioId.get()), 3000,
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
        username = new TextField("Username");
    	//pass = new PasswordField("Password");
        active = new Checkbox("Activated");
    	role = new ComboBox("Role");
    	
        Component[] fields = new Component[]{username,active,role};

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
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        buttonLayout.add(save,delete, cancel);
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

    private void populateForm(Usuario usuario) {
        this.usuario = usuario;
        binder.readBean(this.usuario);

    }
}
