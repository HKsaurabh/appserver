package com.appserver.controller;
import java.util.List;
import com.appserver.service.S3Wrapper;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.appserver.bean.Image;
import com.appserver.service.ImageService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Created by Saurabh on 15-04-2017.
 */
@RestController
public class ImageController {

        @Autowired
        private ImageService imageService;
        @Autowired
        private S3Wrapper s3Wrapper;

    @RequestMapping(value = "/index.jsp", method = RequestMethod.GET)
    public class RootController {
        public String root() {
            return "index";
        }

        @RequestMapping(value = "/image", method = RequestMethod.GET, produces = "application/json")
        public ResponseEntity<List<Image>> images() {

            HttpHeaders headers = new HttpHeaders();
            List<Image> images = imageService.getImages();

            if (images == null) {
                return new ResponseEntity<List<Image>>(HttpStatus.NOT_FOUND);
            }
            headers.add("Number Of Records Found", String.valueOf(images.size()));
            return new ResponseEntity<List<Image>>(images, headers, HttpStatus.OK);
        }

        @RequestMapping(value = "/image/{id}", method = RequestMethod.GET)
        public ResponseEntity<Image> getImage(@PathVariable("id") Long imageId) {
            Image image = imageService.getImage(imageId);
            if (image == null) {
                return new ResponseEntity<Image>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<Image>(image, HttpStatus.OK);
        }

        @RequestMapping(value = "/image/delete/{id}", method = RequestMethod.DELETE)
        public ResponseEntity<Image> deleteImage(@PathVariable("id") Long imageId) {
            HttpHeaders headers = new HttpHeaders();
            Image image = imageService.getImage(imageId);
            if (image == null) {
                return new ResponseEntity<Image>(HttpStatus.NOT_FOUND);
            }
            imageService.deleteImage(imageId);
            headers.add("Image Deleted - ", String.valueOf(imageId));
            return new ResponseEntity<Image>(image, headers, HttpStatus.NO_CONTENT);
        }

        @RequestMapping(value = "/image", method = RequestMethod.POST, produces = "application/json")
        public ResponseEntity<Image> createImage(@RequestBody Image image) {
            HttpHeaders headers = new HttpHeaders();
            if (image == null) {
                return new ResponseEntity<Image>(HttpStatus.BAD_REQUEST);
            }
            imageService.createImage(image);
            headers.add("Image Created  - ", String.valueOf(image.getImageId()));
            return new ResponseEntity<Image>(image, headers, HttpStatus.CREATED);
        }

        @RequestMapping(value = "/image/{id}", method = RequestMethod.PUT)
        public ResponseEntity<Image> updateImage(@PathVariable("id") Long imageId, @RequestBody Image image) {
            HttpHeaders headers = new HttpHeaders();
            Image isExist = imageService.getImage(imageId);
            if (isExist == null) {
                return new ResponseEntity<Image>(HttpStatus.NOT_FOUND);
            } else if (image == null) {
                return new ResponseEntity<Image>(HttpStatus.BAD_REQUEST);
            }
            imageService.updateImage(image);
            headers.add("Image Updated  - ", String.valueOf(imageId));
            return new ResponseEntity<Image>(image, headers, HttpStatus.OK);
        }

        @RequestMapping(value = "/upload", method = RequestMethod.POST)
        public List<PutObjectResult> upload(@RequestParam("file") MultipartFile[] multipartFiles) {
            return s3Wrapper.upload(multipartFiles);
        }

    }
}