package com.example.backRadiology.infraestructure.services.login;
import com.example.backRadiology.exceptions.FTPErrors;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

import java.io.File;
import java.util.List;

public interface IFtpClientService {
    void connectToFTP(String host, int port, String username, String password) throws FTPErrors;

    void uploadImage(List<MultipartFile> dicomFile, String personName, String cedula) throws Exception;

    void downloadFileFromFTP(String ftpRelativePath, String copytoPath) throws FTPErrors
            ;
    void disconnectFTP() throws FTPErrors;


}
