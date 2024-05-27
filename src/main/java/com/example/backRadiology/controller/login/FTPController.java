package com.example.backRadiology.controller.login;
import org.springframework.web.multipart.MultipartFile;
import com.example.backRadiology.exceptions.FTPErrors;
import com.example.backRadiology.exceptions.ErrorMessage;
import com.example.backRadiology.infraestructure.services.login.FtpClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api")
public class FTPController {

    @Autowired
    private FtpClientService ftpService;


    @PostMapping("/upload/ftp")
    public String uploadImage(@RequestParam("dicomFiles") List<MultipartFile> dicomFiles,
                              @RequestParam("personName") String personName,
                              @RequestParam("cedula") String cedula) {
        try {

            if (!dicomFiles.isEmpty()) {
                try {
                    ftpService.uploadImage(dicomFiles, personName, cedula);
                    return "Imagen subida con éxito al servidor FTP.";
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Error al subir la imagen al servidor FTP: " + e.getMessage();
                }
            } else {
                return "Error: La imagen está vacía.";
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "mal";
        }


    }
}


