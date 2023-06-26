
//    public Map<String, List<String>> uploadImages(MultipartFile[] files, long postId){
//        Map<String, List<String>> map = new HashMap<>();
//        map.put("OK", new ArrayList<>());
//        map.put("KO", new ArrayList<>());
//
//        for (MultipartFile file : files){
//            try {
//                String newFileName = postId + "_" + file.getOriginalFilename();
//                Path p = Paths.get(path + newFileName);
//                Files.write(p, file.getBytes());
//                map.get("OK").add(newFileName);
//            } catch (IOException e) {
//                map.get("KO").add(file.getOriginalFilename() + ": " + e.getMessage());
//            }
//        }
//        return map;
//    }

////    public ResponseEntity<?> globalCheckImages(long postId, MultipartFile[] files, ImagePosition imPos){
////
////        int maxImages = ImagePosition.CON.maxImages + ImagePosition.HEA.maxImages + ImagePosition.POS.maxImages;
////        int maxSize = imPos.size;
////        int maxHeight = imPos.height;
////        int maxWidth = imPos.width;
////
////        if(checkMaxNumberImagesExceded(files, postId, maxImages))
////            return new ResponseEntity<>("Max file number threshold is " + maxImages, HttpStatus.BAD_REQUEST);
////
////        Set<String> checkMsg = checkSize(files, maxSize);
////        if(!checkMsg.isEmpty())
////            return new ResponseEntity<>("Files too large" + checkMsg, HttpStatus.BAD_REQUEST);
////
////        checkMsg = checkDimension(files, maxHeight, maxWidth);
////        if(!checkMsg.isEmpty())
////            return new ResponseEntity<>("Files px dimensions too large" + checkMsg, HttpStatus.BAD_REQUEST);
////
////        checkMsg = checkExtension(files);
////        if(checkMsg.isEmpty())
////            return new ResponseEntity<>("Files extension not admissed" + checkMsg, HttpStatus.BAD_REQUEST);
////
////
////        Map<String, List<String>> map = uploadImages(files, postId);
////        List<String> fileToPersist = map.get("OK");
////        List<PostImage> postImageList = new ArrayList<>();
////        for(String s : fileToPersist){
////            postImageList.add(new PostImage(new PostImageId(new Post(postId), s), imPos));
////        }
////        repo.saveAll(postImageList);
////        String error = map.get("KO").toString();
////        return new ResponseEntity<>("New images added to post " +
////                map.get("OK").size() + "-> " +
////                "fallite " + map.get("KO").size() + error, HttpStatus.CREATED);
////    }
//
//    @Transactional
//    public ResponseEntity<?> delete(long postId, Set<String> filesToDelete){
//
//        repo.deleteAllByPostImageIdPostIdAndPostImageIdFilenameIn(postId, filesToDelete); // postId_nomefile.estensione
//        Map<String, List<String>> map = deleteImages(filesToDelete);
//        String error = map.get("KO").toString();
//        return new ResponseEntity<>("Deleted images: "+map.get("OK").size()+"\nDelete failed: "+
//                map.get("KO").size()+" -> "+error, HttpStatus.OK);
//    }
//
//
////    @Transactional
////    public ResponseEntity<?> deleteImagesFromPost(long postId, Set<String> filesToDelete) {
////        Map<String, List<String>> map = new HashMap<>();
////        map.put("OK", new ArrayList<>());
////        map.put("KO", new ArrayList<>());
////        // trovare i record sul db da cancellare
////        // eseguirne la cancellazione fisica sul db
////        repo.deleteAllByPostImageIdPostIdAndPostImageIdFilenameIn(postId,filesToDelete);
////        // eliminare i file fisici dalla cartella di rete
////        filesToDelete.forEach(fileName -> {
////            try {
////                Files.delete(
////                        Paths.get(path + fileName)
////                );
////                map.get("OK").add(fileName);
////            } catch (IOException e) {
////                map.get("KO").add(fileName + ": " + e.getMessage());
////            }
////        });
////        return new ResponseEntity<> ("Images deleted from post " +
////                map.get("OK").size() + "-> " +
////                "fallite " + map.get("KO").size() + map.get("KO"), HttpStatus.OK);
////    }
//}
package it.cgmconsulting.myblog.model.service;

import it.cgmconsulting.myblog.model.data.EmbeddablesId.PostImageId;
import it.cgmconsulting.myblog.model.data.common.ImagePosition;
import it.cgmconsulting.myblog.model.data.entity.Post;
import it.cgmconsulting.myblog.model.data.entity.PostImage;
import it.cgmconsulting.myblog.model.data.payload.response.UploadFileResponse;
import it.cgmconsulting.myblog.model.repository.PostImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class PostImageService {

    private final PostImageRepository postImageRepository;
    private final PostService postService;
    private final AvatarService avatarService;

    @Value("${app.post.extensions}")
    private String[] extensions;
    @Value("${app.post.path}")
    private String path;

    private List<UploadFileResponse> checkMaxNumberImages(long postId, MultipartFile[] files, ImagePosition position) {
        log.info("----- number --- " + files.toString());

        List<UploadFileResponse> result = new ArrayList<>();
        long countUploadedImages = postImageRepository.countByPostImageIdPostIdAndImagePosition(postId, position);
        if ((countUploadedImages + files.length) > position.getMaxImages()) {
            result.add(new UploadFileResponse(position.getDescription(), null, null, "Max file number threshold for preview image is " + position.getMaxImages()));
            for (int i = 0; i < files.length; i++) {
                files[i] = null;
            }
        } else
            result.add(new UploadFileResponse(position.getDescription(), null, "Check max file number: ok", null));

        return result;
    }

    private List<UploadFileResponse> checkSize(MultipartFile[] files, ImagePosition imagePosition) {
        log.info("----- size --- " + files.toString());
        List<UploadFileResponse> result = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            if (files[i] != null)
                if (files[i].getSize() > imagePosition.getSize() || files[i].isEmpty()) {
                    result.add(new UploadFileResponse(imagePosition.getDescription(), files[i].getOriginalFilename(),
                            null, "File too large or empty"));
                    files[i] = null;
                } else
                    result.add(new UploadFileResponse(imagePosition.getDescription(), files[i].getOriginalFilename(),
                            "File size ok", null));
        }
        return result;
    }

    private List<UploadFileResponse> checkDimension(MultipartFile[] files, ImagePosition imagePosition) {
        log.info("----- dimension --- " + files.toString());
        List<UploadFileResponse> result = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            BufferedImage bf = null;
            if (files[i] != null) {
                bf = avatarService.fromMultipartFileBufferedImage(files[i]);
                if (bf != null) {
                    if (bf.getHeight() > imagePosition.getHeight() || bf.getWidth() > imagePosition.width) {
                        result.add(new UploadFileResponse(imagePosition.getDescription(),
                                files[i].getOriginalFilename(),
                                null,
                                "Wrong width or heigth"));
                        files[i] = null;
                    } else
                        result.add(new UploadFileResponse(imagePosition.getDescription(),
                                files[i].getOriginalFilename(),
                                "Right width and heigth!",
                                null));
                }
            }
        }
        return result;
    }

    private List<UploadFileResponse> checkExtension(MultipartFile[] files, ImagePosition imagePosition) throws IOException {
        log.info("----- extensions --- " + files.toString());
        List<UploadFileResponse> result = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            if (files[i] != null) {
                String filename = files[i].getOriginalFilename();
                String ext;
                try {
                    ext = filename.substring(filename.lastIndexOf(".") + 1);
                    if (Arrays.stream(extensions).noneMatch(ext::equalsIgnoreCase)) {
                        result.add(new UploadFileResponse(imagePosition.getDescription(),
                                files[i].getOriginalFilename(),
                                null,
                                "Wrong extension, not in: " + extensions));
                        files[i] = null;
                    } else {
                        result.add(new UploadFileResponse(imagePosition.getDescription(),
                                files[i].getOriginalFilename(),
                                "Extension check ok!",
                                null));
                        files[i] = null;
                    }
                } catch (NullPointerException e) {
                    result.add(new UploadFileResponse(imagePosition.getDescription(),
                            files[i].getOriginalFilename(), null,
                            "Something went wrong during extension check"));
                }
            }
        }
        return result;
    }

    @Transactional
    public List<UploadFileResponse> uploadImages(MultipartFile[] files, long postId, ImagePosition imagePosition) {
        log.info("----- extensions --- " + files.toString());
        List<UploadFileResponse> result = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            if (files[i] != null) {
                try {
                    String newFilename = postId + "_" + files[i].getOriginalFilename();
                    Path path = Paths.get(this.path + newFilename);
                    Files.write(path, files[i].getBytes());
                    result.add(new UploadFileResponse(imagePosition.getDescription(),
                            files[i].getOriginalFilename(),
                            "File uploaded",
                            null));
                    postImageRepository.save(new PostImage(new PostImageId(new Post(postId),newFilename), imagePosition));
                } catch (IOException e) {
                    result.add(new UploadFileResponse(imagePosition.getDescription(),
                            files[i].getOriginalFilename(),
                            "File uploaded",
                            null));
                }
            }
        }
        return result;
    }

    /*

            public Map<String, List<String>> deleteImages(Set<String> filesToDelete){
                Map<String, List<String>> map = new HashMap<>();
                map.put("OK", new ArrayList<>());
                map.put("KO", new ArrayList<>());
                for (String file : filesToDelete) {
                    try {
                        Path path = Paths.get(imagePath+file);
                        Files.delete(path);
                        map.get("OK").add(file);
                    } catch (IOException e) {
                        map.get("KO").add(file+": "+e.getMessage());
                    }
                }
                return map;
            }
        */
    public List<UploadFileResponse> callGlobalCheckImages(long postId, MultipartFile[] filesP, MultipartFile[] filesH,
                                                          MultipartFile[] filesC) throws IOException {

        List<UploadFileResponse> result = new ArrayList<>();
        result.addAll(globalCheckImages(postId, filesP, ImagePosition.PRE));
        result.addAll(globalCheckImages(postId, filesH, ImagePosition.HDR));
        result.addAll(globalCheckImages(postId, filesC, ImagePosition.CON));

        return result;

    }

    public List<UploadFileResponse> globalCheckImages(long postId, MultipartFile[] files, ImagePosition position) throws IOException {

        List<UploadFileResponse> finalResult = new ArrayList<>();

        if (files != null) {
            finalResult.addAll(checkMaxNumberImages(postId, files, position));
            finalResult.addAll(checkSize(files, position));
            finalResult.addAll(checkDimension(files, position));
            finalResult.addAll(checkExtension(files, position));
            finalResult.addAll(uploadImages(files, postId, position));
        }

        return finalResult;

    }

    @Transactional
    public ResponseEntity<?> delete(long postId, Set<String> filesToDelete){

        postImageRepository.deleteAllByPostImageIdPostIdAndPostImageIdFilenameIn(postId, filesToDelete); // postId_nomefile.estensione
        Map<String, List<String>> map = deleteImages(filesToDelete);
        String error = map.get("KO").toString();
        return new ResponseEntity<>("Deleted images: "+map.get("OK").size()+"\nDelete failed: "+
                map.get("KO").size()+" -> "+error, HttpStatus.OK);
    }

    public Map<String, List<String>> deleteImages(Set<String> filesToDelete){
        Map<String, List<String>> map = new HashMap<>();
        map.put("OK", new ArrayList<>());
        map.put("KO", new ArrayList<>());
        for (String file : filesToDelete) {
            try {
                Path p = Paths.get(path+file);
                Files.delete(p);
                map.get("OK").add(file);
            } catch (IOException e) {
                map.get("KO").add(file+": "+e.getMessage());
            }
        }
        return map;
    }

/*
    public ResponseEntity<?> globalCheckImages(long postId, MultipartFile[] files, ImagePosition position) throws IOException {

        if(!checkMaxNumberImages(files, postId))
            return new ResponseEntity<>("Max file number threshold is "+maxNumberImages, HttpStatus.BAD_REQUEST);

        Set<String> checkSizeMsg = checkSize(files);
        if(checkSizeMsg.size() > 0)
            return new ResponseEntity<>("Files too large: "+checkSizeMsg, HttpStatus.BAD_REQUEST);

        Set<String> checkDimensionMsg = checkDimension(files);
        if(checkDimensionMsg.size() > 0)
            return new ResponseEntity<>("Wrong width or height: "+checkDimensionMsg, HttpStatus.BAD_REQUEST);

        Set<String> checkExtensionsMsg = checkExtension(files);
        if(checkExtensionsMsg.size() > 0)
            return new ResponseEntity<>("Extensions not allowed: "+checkExtensionsMsg, HttpStatus.BAD_REQUEST);

        Map<String, List<String>> map = uploadImages(files, postId);
        List<String> fileToPersist = map.get("OK");
        List<PostImage> pi = new ArrayList<>();
        for(String s : fileToPersist){
            pi.add(new PostImage(new PostImageId(new Post(postId), s)));
        }
        postImageRepository.saveAll(pi);

        String error = map.get("KO").toString();

        return new ResponseEntity<>("Uploaded images: "+map.get("OK").size()+"\nUpload failed: "+map.get("KO").size()+" -> "+error, HttpStatus.OK);
    }



 */
}



