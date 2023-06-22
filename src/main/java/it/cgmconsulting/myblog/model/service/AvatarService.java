package it.cgmconsulting.myblog.model.service;

import it.cgmconsulting.myblog.model.data.EmbeddablesId.AvatarId;
import it.cgmconsulting.myblog.model.data.entity.Avatar;
import it.cgmconsulting.myblog.model.data.entity.User;
import it.cgmconsulting.myblog.model.repository.AvatarRepository;
import it.cgmconsulting.myblog.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;


@Service
@RequiredArgsConstructor
public class AvatarService {

    @Value("${app.avatar.size}")
    long avatarMaxSize;
    @Value("${app.avatar.width}")
    private int avatarWidth;
    @Value("${app.avatar.height}")
    private int avatarHeight;
    @Value("${app.avatar.extensions}")
    private String[] extensions;

    private final AvatarRepository repo;

    private boolean checkSize(MultipartFile file){
        return  !file.isEmpty() ||
                file.getSize() <= avatarMaxSize;
    }

    private BufferedImage fromMultipartFileBufferedImage(MultipartFile file){
        BufferedImage bf = null;
        try {
            bf = ImageIO.read(file.getInputStream());
            return bf;
        } catch (IOException e) {
            return null;
        }
    }

    private boolean checkDimension(MultipartFile file){
        BufferedImage bf = fromMultipartFileBufferedImage(file);
        return bf != null &&
               bf.getHeight() <= avatarHeight &&
               bf.getWidth() <= avatarWidth;

//        if(bf != null){
//            if(bf.getHeight() > avatarHeight || bf.getWidth() > avatarWidth){
//                return false;
//            }
//            return true;
//        } else {
//            return false;
//        }
    }

    private boolean cheExtension(MultipartFile file){
        String filname = file.getOriginalFilename(); //pippo.gif
        String ext;
        try{
            ext = filname.substring(filname.lastIndexOf(".") + 1);
        } catch(NullPointerException e){
            return false;
        }
        return Arrays.stream(extensions).anyMatch(ext::equalsIgnoreCase);
    }
    public ResponseEntity<?> avatar(MultipartFile file, UserPrincipal principal) throws IOException{
        if(!checkSize(file))
            return new ResponseEntity<>("File too large", HttpStatus.BAD_REQUEST);
        if(!checkDimension(file))
            return new ResponseEntity<>("Wrong file height or width", HttpStatus.BAD_REQUEST);
        if(!cheExtension(file))
            return new ResponseEntity<>("File type not allowed", HttpStatus.BAD_REQUEST);
        Avatar avatar = new Avatar(new AvatarId(new User(principal.getId())), file.getOriginalFilename(),
                file.getContentType(), file.getBytes());
        repo.save(avatar);
        return new ResponseEntity<>("Avatar succesfully updated", HttpStatus.OK);

    }

}
