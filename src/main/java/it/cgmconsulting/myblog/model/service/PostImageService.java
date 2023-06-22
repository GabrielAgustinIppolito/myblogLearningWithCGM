package it.cgmconsulting.myblog.model.service;

import it.cgmconsulting.myblog.model.data.EmbeddablesId.PostImageId;
import it.cgmconsulting.myblog.model.data.entity.Post;
import it.cgmconsulting.myblog.model.data.entity.PostImage;
import it.cgmconsulting.myblog.model.repository.PostImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PostImageService {

    @Value("${app.post.size}")
    private long size;
    @Value("${app.post.maxImages}")
    private int maxImages;
    @Value("${app.post.width}")
    private int width;
    @Value("${app.post.height}")
    private int height;
    @Value("${app.post.path}")
    private String path;
    @Value("${app.post.extensions}")

    private String[] extensions;


    private final PostImageRepository repo;
    private final AvatarService avatarService;

    private boolean checkMaxNumberImagesExceded(MultipartFile[] files, long postId){
        long countUploadedImages = repo.countByPostImageIdPostId(postId);
        return files.length + countUploadedImages > maxImages;
    }

    private Set<String> checkSize(MultipartFile[] files){
        Set<String> msg = new HashSet<>();
        for(MultipartFile file : files){
            if(file.getSize() > size || file.isEmpty()){
                msg.add(file.getOriginalFilename());
            }
        }
        return msg;
    }

    private Set<String> checkDimension(MultipartFile[] files) {
        Set<String> msg = new HashSet<>();
        for (MultipartFile file : files) {
            BufferedImage bf = avatarService.fromMultipartFileBufferedImage(file);
            if (bf != null)
                if (bf.getHeight() > height || bf.getWidth() > width)
                    msg.add(file.getOriginalFilename());
        }
        return msg;
    }

    public Set<String> checkExtension(MultipartFile[] files){
        Set<String> msg = new HashSet<>();
        String filname;
        String ext;
        for (MultipartFile file : files) {
            filname = file.getOriginalFilename();
            try{
                ext = filname.substring(filname.lastIndexOf(".") + 1);
                if(Arrays.stream(extensions).anyMatch(ext::equalsIgnoreCase)){
                    msg.add(file.getOriginalFilename());
                }
            } catch(NullPointerException e){
                msg.add("Qualcosa è andato storto");
            }
        }
        return msg;
    }

    public Map<String, List<String>> uploadImages(MultipartFile[] files, long postId){
        Map<String, List<String>> map = new HashMap<>();
        map.put("OK", new ArrayList<>());
        map.put("KO", new ArrayList<>());

        for (MultipartFile file : files){
            try {
                String newFileName = postId + "_" + file.getOriginalFilename();
                Path p = Paths.get(path + newFileName);
                Files.write(p, file.getBytes());
                map.get("OK").add(newFileName);
            } catch (IOException e) {
                map.get("KO").add(file.getOriginalFilename() + ": " + e.getMessage());
            }
        }
        return map;
    }
    public ResponseEntity<?> addImages(long postId, MultipartFile[] files){
        if(checkMaxNumberImagesExceded(files, postId))
            return new ResponseEntity<>("Max file number threshold is " + maxImages, HttpStatus.BAD_REQUEST);

        Set<String> checkMsg = checkSize(files);
        if(!checkMsg.isEmpty())
            return new ResponseEntity<>("Files too large" + checkMsg, HttpStatus.BAD_REQUEST);

        checkMsg = checkDimension(files);
        if(!checkMsg.isEmpty())
            return new ResponseEntity<>("Files px dimensions too large" + checkMsg, HttpStatus.BAD_REQUEST);

        checkMsg = checkExtension(files);
        if(checkMsg.isEmpty())
            return new ResponseEntity<>("Files extension not admissed" + checkMsg, HttpStatus.BAD_REQUEST);


        Map<String, List<String>> map = uploadImages(files, postId);
        List<String> fileToPersist = map.get("OK");
        List<PostImage> postImageList = new ArrayList<>();
        for(String s : fileToPersist){
            postImageList.add(new PostImage(new PostImageId(new Post(postId), s)));
        }
        repo.saveAll(postImageList);
        String error = map.get("KO").toString();
        return new ResponseEntity<>("New images added to post " +
                map.get("OK").size() + "-> " +
                "fallite " + map.get("KO").size() + error, HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<?> deleteImagesFromPost(long postId, Set<String> filesToDelete) {
        Map<String, List<String>> map = new HashMap<>();
        map.put("OK", new ArrayList<>());
        map.put("KO", new ArrayList<>());
        // trovare i record sul db da cancellare
        // eseguirne la cancellazione fisica sul db
        repo.deleteAllByPostImageIdPostIdAndPostImageIdFilenameIn(postId,filesToDelete);
        // eliminare i file fisici dalla cartella di rete
        filesToDelete.forEach(file -> {
            try {
                Files.delete(
                        Paths.get(path + file)
                );
                map.get("OK").add(file);
            } catch (IOException e) {
                map.get("KO").add(file + ": " + e.getMessage());
            }
        });
        return new ResponseEntity<> ("Images deleted from post " +
                map.get("OK").size() + "-> " +
                "fallite " + map.get("KO").size() + map.get("KO"), HttpStatus.OK);
    }
}
