package sample.service;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.model.Publication;
import sample.model.Subscription;
import sample.repository.PublicationRepository;
import sample.repository.SubscriptionRepository;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by cristi on 03/03/2016.
 */
@Log4j
@Service
public class PubsubscriptionService {


    @Autowired
    private PublicationRepository publicationRepository;

    @Autowired
    private FileService fileService;

    public Map<Long, Subscription> getSubscriptionsMapByPublicationId(List<Subscription> subscriptions) {

        return subscriptions.stream().collect(
                Collectors.toMap(s -> s.getPublication().getId(), s -> s));
    }

    @Transactional
    public void storePublication(Publication publication, byte[] content) throws IOException {
        publication = publicationRepository.save(publication);
        fileService.storeFile(content, publication.getId());
    }
}
