package sample.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import sample.model.Publication;
import sample.repository.PublicationRepository;
import sample.repository.SubscriptionRepository;
import sample.service.CryptoService;
import sample.service.FileService;
import sample.service.UserService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;


@RestController
@PreAuthorize("hasAuthority('PUBLISHER') or hasAuthority('VIEWER')")
public class PublicationController {


	@Autowired
	private PublicationRepository publicationRepository;

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Autowired
	private UserService userService;

    @Autowired
    private CryptoService cryptoService;

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
	public DataTablesOutput<Publication> getPublications(@Valid DataTablesInput input) {
		return publicationRepository.findAll(input);
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

        // current user becomes the publisher
        publication.setPublisher(userService.getCurrentUser(activeUser));
		publication = publicationRepository.save(publication);
		fileService.storeFile(file.getBytes(), publication.getId());

		return new ModelAndView("publication", "publication", new Publication());
	}


	@RequestMapping(value ={"/publication/{publicationId}"}, method = RequestMethod.GET)
	public HttpEntity<byte[]> getPublicationContent(@Valid @PathVariable Long publicationId, @AuthenticationPrincipal org.springframework.security.core.userdetails.User activeUser) throws IOException, CryptoService.CryptoException {

        // security validation that current user is subscribed to the publication
        Publication publication = publicationRepository.findOne(publicationId);
        if(subscriptionRepository.countByUserAndPublication(userService.getCurrentUser(activeUser), publication) == 0) {
            throw new SecurityException("not authorized - user " + activeUser.getUsername() + " not owner of publicationId: " + publicationId);
        }

        byte[] documentBody = fileService.getFileAsBytes(publicationId);

        // encrypt content
        byte[] encryptedDocumentBody = cryptoService.encrypt("MY SECRET KEY!!!", documentBody);

		HttpHeaders header = prepareHttpHeaders();
		header.setContentLength(encryptedDocumentBody.length);

		return new HttpEntity<byte[]>(encryptedDocumentBody, header);

	}

	private HttpHeaders prepareHttpHeaders() {
		HttpHeaders header = new HttpHeaders();
		header.set("Content-Type", "application/pdf");
		header.set("Accept-Ranges", "bytes");
		return header;
	}


}
