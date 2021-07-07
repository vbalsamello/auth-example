package com.example.application.views.login;

import com.example.application.data.entity.Role;
import com.example.application.data.entity.Usuario;
import com.example.application.data.service.AuthService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "register")
@PageTitle("Register")
public class RegisterView extends Composite{

	private TextField usuario = new TextField("Username");
	private PasswordField pass = new PasswordField("Password");
	private ComboBox<Role> role = new ComboBox("Role");
	private Usuario usuarioBase = new Usuario();
	private BeanValidationBinder<Usuario> binder = new BeanValidationBinder<>(Usuario.class);
	
	private final AuthService authService;
	
	public RegisterView(AuthService authService) {
		this.authService = authService;
	}
	@Override
	protected Component initContent(){
		
	var confirmPass =new PasswordField("ConfirmePassword");	
		//binder.bindInstanceFields(this);
		binder.bind(usuario,Usuario::getUsername,Usuario::setUsername);
		binder.bind(pass,Usuario::getPassword,Usuario::setPassword);
		binder.bind(role,Usuario::getRole,Usuario::setRole);
		binder.setBean(usuarioBase);
		
		role.setItems(Role.values());
		
		var boton = new Button("Send");
		boton.addClickListener(event ->{
			if(binder.isValid()) {
				authService.register(usuarioBase);
				Notification.show("check the log");
			}else {
				Notification.show("Binder false");
			}
			
			if(usuario.isInvalid() || pass.isInvalid() || confirmPass.isInvalid()) {
				Notification.show("Formulario incompleto.");
			}else {
				if(usuarioBase.getUsername().trim().isEmpty()) {
					Notification.show("usuario vacio");
				}else if(usuarioBase.getPasswordHash().isEmpty()){
					Notification.show("pass vacio");
				}else if(!pass.getValue().equals(confirmPass.getValue())) {
					Notification.show("pass diferentes");			
				}else {
						
					authService.register(usuarioBase);
					Notification.show("check the log");
				}
				
			}
			
		});
		
		
		return new VerticalLayout(
				new H2("Register"),
				usuario,
				role,
				pass,
				confirmPass,
				boton						
				);
	}
	
	
}
