package com.example.application.data.service;

import java.util.List;


import com.example.application.data.entity.Role;
import com.example.application.data.entity.Usuario;
import com.example.application.views.admin.AdminUserView;
import com.example.application.views.admin.AdminView;
import com.example.application.views.home.HomeView;
import com.example.application.views.logout.LogoutView;
import com.example.application.views.main.MainView;
import com.sun.jna.platform.win32.Netapi32Util.User;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService {

	public class AuthorizedRoute {
		public String route;
		public String name; 
		public Class<? extends Component> view;
		
		public AuthorizedRoute(String route,
		 String name,
		 Class<? extends Component> view) {
			this.name = name;
			this.route = route;
			this.view = view;
		}
		
	}
	
	public class AuthException extends Exception {		
	}

	private final UsuarioService usuarioService;
	
	public AuthService(UsuarioService usuarioService) {
		
		this.usuarioService = usuarioService;
	}
	
	public void authenticate(String username, String password) throws AuthException {
		
		var usuario = usuarioService.getUsuario(username);
		if(usuario != null && usuario.checkPassword(password)) {
			VaadinSession.getCurrent().setAttribute(Usuario.class,usuario);
			createRoutes(usuario.getRole());
		}else {
			
			throw new AuthException();
		}
	}

	private void createRoutes(Role role) {
		getAuthorizedRoutes(role).stream()
			.forEach(route -> RouteConfiguration.forSessionScope()
					.setRoute(route.route,route.view, MainView.class));
		
	}
	
	public List<AuthorizedRoute> getAuthorizedRoutes(Role role){
		var routes = new ArrayList<AuthorizedRoute>();
		
		if(role.equals(Role.USER)) {
			routes.add(new AuthorizedRoute("home","Home",HomeView.class));
			routes.add(new AuthorizedRoute("adminuser","Usuarios",AdminUserView.class));
			routes.add(new AuthorizedRoute("admin","Mascotas",AdminView.class));
			routes.add(new AuthorizedRoute("logout","Logout",LogoutView.class));
			
		}else if(role.equals(Role.ADMIN)) {
			routes.add(new AuthorizedRoute("home","Home",HomeView.class));
			routes.add(new AuthorizedRoute("admin","Admin",AdminView.class));
			
			routes.add(new AuthorizedRoute("logout","Logout",LogoutView.class));
		}
		
		return routes;
	}
	
	public void register(Usuario usuario) {
		usuario.setRole(Role.USER);
		usuarioService.save(usuario);
	}
	
 
}
