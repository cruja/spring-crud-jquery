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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.annotation.JsonView;

import sample.model.Publication;
import sample.model.User;
import sample.repository.PublicationRepository;
import sample.repository.SubscriptionRepository;
import sample.repository.UserRepository;
import sample.service.FileService;
import sample.service.UserService;


@RestController
@PreAuthorize("hasAuthority('PUBLISHER') or hasAuthority('VIEWER')")
public class PublicationController {


	@Autowired
	private PublicationRepository publicationRepository;

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private FileService fileService;

	@RequestMapping(value = { "/publications" }, method = { RequestMethod.GET })
	public ModelAndView addPublication() {
		return new ModelAndView("publication", "publication", new Publication());
	}
	
	@RequestMapping(value = { "/publications/" }, method = { RequestMethod.GET })
	public ModelAndView getPublications() {
		return new ModelAndView("publications");
	}

	/**
	 * REST Controller returning {@link DataTablesOutput}
	 */
	@JsonView(DataTablesOutput.View.class)
	@RequestMapping(value = "/data/publications", method = RequestMethod.GET)
	public DataTablesOutput<Publication> getPublications(@Valid DataTablesInput input) {
		return publicationRepository.findAll(input);
	}

	@RequestMapping(value = "/publications/{id}/delete", method = {RequestMethod.DELETE, RequestMethod.GET})
	public String deletePublications(@Valid @PathVariable Long id, @AuthenticationPrincipal org.springframework.security.core.userdetails.User activeUser, HttpServletResponse response) throws IOException {
		
		Publication publication = publicationRepository.findOne(id);
		
		// security validation that current user is the owner
		userService.validateIfEntityOwner(activeUser, publication);
		
		// remove all publication subscriptions
		subscriptionRepository.deleteByPublication(publication);
		
		publicationRepository.delete(id);
		response.sendRedirect("/publications/");
		return "redirect:/publications/";
	}

	@RequestMapping(value = "/publications/{id}", method = RequestMethod.GET)
	public ModelAndView getPublication(@Valid @PathVariable Long id) {
		Publication publication = publicationRepository.findOne(id);
		return new ModelAndView("publication", "publication", publication);
	}

	@RequestMapping(value = "/publications/", method = RequestMethod.POST)
	public ModelAndView updatePublication(@Valid Publication publication, BindingResult bindingResult,
			@RequestParam("file") MultipartFile file,
			@AuthenticationPrincipal org.springframework.security.core.userdetails.User activeUser,
			HttpServletResponse response) throws IOException {

		if (bindingResult.hasErrors()) {
			response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
			return new ModelAndView("publication", "publication", publication);
		}
		
		if (publication.getId() != null) {
			// security validation that current user is the owner for UPDATE
			userService.validateIfEntityOwner(activeUser, publication);
		}
		
		// logged user
		User publisher = userRepository.findOne(Long.valueOf(activeUser.getUsername()));
	
		publication.setPublisher(publisher);
		publication = publicationRepository.save(publication);

		fileService.storeFile(file, publication.getId());

		return new ModelAndView("publication", "publication", new Publication());
	}

	
}
