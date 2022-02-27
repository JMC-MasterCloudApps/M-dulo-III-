package es.codeurjc.mca.practica_1_cloud_ordinaria_2021.image;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

    String createImage(MultipartFile multiPartFile);

    void deleteImage(String image);
}
