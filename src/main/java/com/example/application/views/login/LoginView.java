package com.example.application.views.login;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.example.application.data.service.AuthService;
import com.example.application.data.service.AuthService.AuthException;
import com.example.application.views.home.HomeView;
import com.example.application.views.main.MainView;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;

@Route(value = "login")
@PageTitle("Login")
public class LoginView extends Div {

    public LoginView(AuthService authService) {
        addClassName("login-view");
        var username = new TextField("Username");
        username.setAutofocus(true);
        username.setClearButtonVisible(true); 
        username.setRequired(true);
        username.setMinLength(2);
        username.setErrorMessage("Campo obligatorio");
               
        
        var password = new PasswordField("Password");
        password.setRequired(true);
        
        
        String url = RouteConfiguration.forSessionScope().getUrl(RegisterView.class);

   
        
        add(
        		new H1("Welcome!"),
        		username, 
        		password,
        		new Button("Login", event -> {
        			try {
						authService.authenticate(username.getValue(), password.getValue());
						UI.getCurrent().navigate("home");
					} catch (AuthException e) {
						// TODO Auto-generated catch block
						Notification.show("Wrong credentials.");
					}
        		})
        		,new Anchor(url, "Register")        				
        				
        		);
    }

}
