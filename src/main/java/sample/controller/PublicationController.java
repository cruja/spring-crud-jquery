package sample.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import sample.model.Publication;
import sample.model.User;
import sample.repository.PublicationRepository;
import sample.repository.SubscriptionRepository;
import sample.service.FileService;
import sample.service.UserService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;


@RestController
@PreAuthorize("hasAuthority('PUBLISHER')")
public class PublicationController {


	@Autowired
	private PublicationRepository publicationRepository;

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private FileService fileService;

	@RequestMapping(value = { "/publications" }, method = { RequestMethod.GET })
	public ModelAndView addPublication(@AuthenticationPrincipal org.springframework.security.core.userdetails.User activeUser) {
        return new ModelAndView("publication", "publication",  new Publication());
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
	public DataTablesOutput<Publication> getPublications(@Valid DataTablesInput input, @AuthenticationPrincipal org.springframework.security.core.userdetails.User activeUser) {

		User currentUser = userService.getCurrentUser(activeUser);
		Specification<Publication> andPublisherPublications = (root, query, cb) -> cb.equal(root.get("publisher"), currentUser);
		return publicationRepository.findAll(input, andPublisherPublications);
	}

	@RequestMapping(value = "/publications/{id}/delete", method = {RequestMethod.DELETE, RequestMethod.GET})
	public void deletePublications(@Valid @PathVariable Long id, @AuthenticationPrincipal org.springframework.security.core.userdetails.User activeUser, HttpServletResponse response) throws IOException {
		
		Publication publication = publicationRepository.findOne(id);
		
		// security validation that current user is the owner
		userService.validateIfEntityOwner(activeUser, publication);
		
		// remove all publication subscriptions
		subscriptionRepository.deleteByPublication(publication);
		
		publicationRepository.delete(id);
		response.sendRedirect("/publications/");
	}

	@RequestMapping(value = "/publications/{id}", method = RequestMethod.GET, produces = "text/plain; charset=utf-8")
	public ModelAndView getPublication(@Valid @PathVariable Long id) {
		Publication publication = publicationRepository.findOne(id);
		return new ModelAndView("publication", "publication", publication);
	}

	@RequestMapping(value = "/publications/{id}", method = RequestMethod.GET, produces="application/json; charset=utf-8")
	public Publication getPublicationAsJson(@Valid @PathVariable Long id) {
		return publicationRepository.findOne(id);
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

        // set current user is the publisher
        publication.setPublisher(userService.getCurrentUser(activeUser));
		publication = publicationRepository.save(publication);
		fileService.storeFile(file.getBytes(), publication.getId());

		return new ModelAndView("publication", "publication", new Publication());
	}

}
