package sample.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.extern.log4j.Log4j;
import sample.model.User;
import sample.model.User.Status;
import sample.repository.UserRepository;

@Log4j
@RestController
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {

	@Autowired
	private UserRepository userRepository;


	@RequestMapping(value = { "/users" }, method = { RequestMethod.GET })
	public ModelAndView newUser() {
		return new ModelAndView("user", "user", new  sample.model.User());
	}
	
	@RequestMapping(value = { "/users/" }, method = { RequestMethod.GET })
	public ModelAndView getUsers() {
		return new ModelAndView("users");
	}

	/**
	 * REST Controller returning {@link DataTablesOutput}
	 */
	@JsonView(DataTablesOutput.View.class)
	@RequestMapping(value = "/data/users", method = RequestMethod.GET)
	public DataTablesOutput<User> getUsers(@Valid DataTablesInput input) {
		return userRepository.findAll(input);
	}

	@RequestMapping(value = "/users/{id}/block", method = {RequestMethod.DELETE, RequestMethod.GET})
	public void blockUser(@Valid @PathVariable Long id, @AuthenticationPrincipal org.springframework.security.core.userdetails.User activeUser, HttpServletResponse response) throws IOException {
		
		User user = userRepository.findOne(id);
		user.setStatus(Status.BLOCKED);
		userRepository.save(user);
		response.sendRedirect("/users/");
	}

	@RequestMapping(value = "/users/{id}/activate", method = {RequestMethod.GET})
	public void activateUser(@Valid @PathVariable Long id, @AuthenticationPrincipal org.springframework.security.core.userdetails.User activeUser, HttpServletResponse response) throws IOException {
		
		User user = userRepository.findOne(id);
		user.setStatus(Status.ACTIVE);
		userRepository.save(user);
		response.sendRedirect("/users/");
	}

	
	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET, produces = "text/plain; charset=utf-8")
	public ModelAndView getUser(@Valid @PathVariable Long id) {
		return new ModelAndView("user", "user", getUserAsJson(id));
	}

	@RequestMapping(value = "/users/{id}", method = RequestMethod.GET, produces="application/json; charset=utf-8")
	public User getUserAsJson(@Valid @PathVariable Long id) {
		User user = userRepository.findOne(id);
		
		// clear password
		if (user != null) {
			user.setPassword("");
		}
		
		return user;
	}

	
	@RequestMapping(value = "/users/", method = RequestMethod.POST)
	public ModelAndView updatePublication(@Valid sample.model.User user, BindingResult bindingResult,			
			@AuthenticationPrincipal org.springframework.security.core.userdetails.User activeUser,
			HttpServletResponse response) throws IOException {

		if (bindingResult.hasErrors()) {
			response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
		} 
		else {
			//encode password
			user.encodePassword(user.getPassword());
			user.setStatus(Status.ACTIVE);
			userRepository.save(user);
		}
		return new ModelAndView("user", "user", user);
	}

	
}
