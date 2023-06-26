package it.cgmconsulting.myblog.model.service;

import it.cgmconsulting.myblog.exception.ResourceNotFoundException;
import it.cgmconsulting.myblog.model.data.common.ImagePosition;
import it.cgmconsulting.myblog.model.data.entity.Category;
import it.cgmconsulting.myblog.model.data.entity.Post;
import it.cgmconsulting.myblog.model.data.entity.User;
import it.cgmconsulting.myblog.model.data.payload.request.PostRequest;
import it.cgmconsulting.myblog.model.data.payload.response.PostBoxesResponse;
import it.cgmconsulting.myblog.model.data.payload.response.PostSearchResponse;
import it.cgmconsulting.myblog.model.repository.CategoryRepository;
import it.cgmconsulting.myblog.model.repository.PostRepository;
import it.cgmconsulting.myblog.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepo;
    private final CategoryRepository categoryRepo;

    public ResponseEntity<?> createPost(PostRequest request, UserPrincipal principal){
        if(postRepo.existsByTitle(request.getTitle())){
            return new ResponseEntity<>("A post with this title is alredy present", HttpStatus.BAD_REQUEST);
        }
        Post p = new Post(request.getTitle(), request.getOverview(), request.getContent(), new User(principal.getId()));
        postRepo.save(p);
        return new ResponseEntity("New Post created", HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<?> updatePost(long postId, PostRequest request, UserPrincipal principal){
        /* Verifica unicità title */
        if(postRepo.existsByTitleAndIdNot(request.getTitle(), postId)){
            return new ResponseEntity<>("Another post with this title is already present", HttpStatus.BAD_REQUEST);
        }
        /* Verifica esistenza post */
        Post p = findPost(postId);
//        if(p == null){
//            return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);
//        } non più necessario grazie al metodo findPost
        /* Facciamo l' update dei dati */
        p.setTitle(request.getTitle());
        p.setOverview(request.getOverview());
        p.setContent(request.getContent());
        p.setAuthor(new User(principal.getId()));
        /* Settiamo il published a null così da non renderlo pubblicabile fino *
         *  a nuova revisione                                                  */
        p.setPublishedAt(null);
        /* repo.save(p.get()); -> Non necessario perché usiamo l'annotazione transactional */
        return new ResponseEntity<>("New Post created", HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<?> publishPost(long postId, String publishedAt) {
        Post p = findPost(postId);
        if(publishedAt.length()<11){
            publishedAt = publishedAt.concat(" 00:00");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(publishedAt, formatter);

        if(dateTime.isBefore(LocalDateTime.now())){
            return new ResponseEntity<>("Selected publication dat is in the past", HttpStatus.BAD_REQUEST);
        }

        if(p == null){
            return new ResponseEntity<>("Post not found", HttpStatus.NOT_FOUND);
        }
        String msg = "Post published";
        if(publishedAt == null) {
            p.setPublishedAt(LocalDateTime.now());
        } else {
            p.setPublishedAt(dateTime);
            msg = "Post date for publishing updated";
        }
        return new ResponseEntity<>(msg, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> addCategories(long postId, Set<String> categories) {
        Post p = findPost(postId);
        Set<Category>  categoriesToAdd = categoryRepo.getCategoriesIn(categories);
        if(categoriesToAdd.isEmpty())
            return new ResponseEntity<>("No categories found", HttpStatus.NOT_FOUND);
        p.setCategories(categoriesToAdd);
        return new ResponseEntity<>("Categories added to post", HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> removeAllCategories(long postId) {
        Post p = findPost(postId);
//        p.setCategories(new HashSet<>());
        p.getCategories().clear();
        return new ResponseEntity<>("Categories removed from post", HttpStatus.OK);
    }

    protected Post findPost(long postId){
        Post p = postRepo.findById(postId).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", postId)
        );
        return p;
    }

    protected Post findVisiblePost(long postId){
        Post p = postRepo.findByIdAndPublishedAtNotNullAndPublishedAtBefore(postId, LocalDateTime.now()).orElseThrow(
                () -> new ResourceNotFoundException("Post", "id", postId)
        );
        return p;
    }

    public ResponseEntity<?> getPostBoxes(int pageNumber, int pageSize, String direction,
                                          String sortBy, String imagePosition){
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.valueOf(direction.toUpperCase()), sortBy);
        Page<PostBoxesResponse> result = postRepo.getPostBoxes(pageable, LocalDateTime.now(), ImagePosition.valueOf(imagePosition));
        List<PostBoxesResponse> list = new ArrayList<>();
        if(result.hasContent()){
            list = result.getContent();
            for(PostBoxesResponse pbr : list){
                pbr.setCategories(postRepo.getCategoriesByPost(pbr.getPostId()));
            }
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
    public ResponseEntity<?> getPostDetail(long postId, String imagePosition){
        return new ResponseEntity<>(postRepo.getPostDetail(postId, ImagePosition.valueOf(imagePosition),
                LocalDateTime.now()), HttpStatus.OK);

    }

    public ResponseEntity<?> getPostByCategory(String categoryName, String imagePosition) {
        List<PostBoxesResponse> list = postRepo.getPostByCategory(categoryName, LocalDateTime.now(),
                                                                    ImagePosition.valueOf(imagePosition ));
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    public ResponseEntity<?> getPostByKeyword(String keyword, boolean isCaseSensitive, boolean isExactMatch) {
        List<PostSearchResponse> list = postRepo.getPostContainsAnywhere(keyword, LocalDateTime.now());
        if (!isCaseSensitive && !isExactMatch) {
            return new ResponseEntity<>(list, HttpStatus.OK);
        }else if (!isCaseSensitive && isExactMatch) {
            List<PostSearchResponse> result = list.stream().filter(psr ->
                        ( isIgnoringCaseExactMacthing(psr.getTitle(),keyword)
                       || isIgnoringCaseExactMacthing(psr.getOverview(),keyword)
                       || isIgnoringCaseExactMacthing(psr.getContent(),keyword))).toList()
            ;
            return new ResponseEntity<>(result, HttpStatus.OK);
        }else if (isCaseSensitive && !isExactMatch) {
            List<PostSearchResponse> result = list.stream().filter(psr ->
                            (psr.getTitle().contains(keyword)
                            || psr.getOverview().contains(keyword)
                            ||psr.getContent().contains(keyword))).toList();
                    ;
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            List<PostSearchResponse> result = list.stream().filter(psr ->
                    ((psr.getTitle().contains(keyword) && isIgnoringCaseExactMacthing(psr.getTitle(),keyword))
                  || (psr.getOverview().contains(keyword) && isIgnoringCaseExactMacthing(psr.getOverview(),keyword))
                  || (psr.getContent().contains(keyword)) && isIgnoringCaseExactMacthing(psr.getContent(),keyword))
            ).toList();
            ;
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    private boolean isIgnoringCaseExactMacthing(String toEvaluate, String keyword){
        return toEvaluate.matches("(?i)" + keyword + " .*")
            || toEvaluate.matches("(?i).* " + keyword + " .*")
            || toEvaluate.matches("(?i).* " + keyword)
            || toEvaluate.matches("(?i).* " + keyword + "\\R.*")
            || toEvaluate.matches("(?i).*\\R" + keyword + "\\R.*")
            || toEvaluate.matches("(?i).*\\R" + keyword + " .*")
            || toEvaluate.matches("(?i).*\\R " + keyword);
    }
}
