package com.example.backRadiology.infraestructure.services.login;


import com.example.backRadiology.exceptions.FTPErrors;
import com.example.backRadiology.exceptions.ErrorMessage;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

@Service
public class FtpClientService implements IFtpClientService {

    FTPClient ftpconnection;
    private Logger logger = LoggerFactory.getLogger(FtpClientService.class);

    @Override
    public void connectToFTP(String host, int port, String username, String password) throws FTPErrors {
        this.ftpconnection = new FTPClient();
        this.ftpconnection.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
        int reply;

        try {
            this.ftpconnection.connect(host);
        } catch (IOException e) {
            ErrorMessage errorMessage = new ErrorMessage(-1, "No fue posible conectarse al FTP a través del host=" + host);
            logger.error(errorMessage.toString());
            throw new FTPErrors(errorMessage);
        }

        reply = this.ftpconnection.getReplyCode();

        if (!FTPReply.isPositiveCompletion(reply)) {

            try {
                this.ftpconnection.disconnect();
            } catch (IOException e) {
                ErrorMessage errorMessage = new ErrorMessage(-2, "No fue posible conectarse al FTP, el host=" + host + " entregó la respuesta=" + reply);
                logger.error(errorMessage.toString());
                throw new FTPErrors(errorMessage);
            }
        }

        try {
            this.ftpconnection.login(username, password);
        } catch (IOException e) {
            ErrorMessage errorMessage = new ErrorMessage(-3, "El puerto=" + port + ", y el pass=**** no fueron válidos para la autenticación.");
            logger.error(errorMessage.toString());
            throw new FTPErrors(errorMessage);
        }

        try {
            this.ftpconnection.setFileType(FTP.BINARY_FILE_TYPE);
        } catch (IOException e) {
            ErrorMessage errorMessage = new ErrorMessage(-4, "El tipo de dato para la transferencia no es válido.");
            logger.error(errorMessage.toString());
            throw new FTPErrors(errorMessage);
        }

        this.ftpconnection.enterLocalPassiveMode();
    }

    private String baseDir;
    private void createFolder(FTPClient ftpconnection, String folderName) throws IOException {

        try {
            connectToFTP("127.0.0.1", Integer.parseInt("14148"),"PABLO","pablo123");
            this.ftpconnection.enterLocalPassiveMode();


            if (!this.ftpconnection.changeWorkingDirectory(uploadDir)) {
                throw new IOException("Directorio base no encontrado en el servidor FTP.");
            }

            IOException ioException = new IOException();
            if (!this.ftpconnection.makeDirectory(folderName)) {
                throw new IOException("No se pudo crear la carpeta para la persona.");
            }

        }catch (Exception IOeException) {
            // Handle it.
        }


    }

    private static final String uploadDir = "/upload";
    @Override
    public void uploadImage(List<MultipartFile> dicomFiles, String personName, String cedula) throws Exception {
        try {
            connectToFTP("127.0.0.1", Integer.parseInt("14148"),"PABLO","pablo123");
            this.ftpconnection.setFileType(FTP.BINARY_FILE_TYPE);
            this.ftpconnection.changeWorkingDirectory(uploadDir);

            // Crear la carpeta con el nombre de la persona y la cédula
            String folderName = personName + "_" + cedula;
            createFolder(ftpconnection, folderName);

            for (MultipartFile dicomFile : dicomFiles){
                String remoteFileName = folderName + "/" + dicomFile.getOriginalFilename();

                InputStream inputStream = dicomFile.getInputStream();
                ftpconnection.changeWorkingDirectory(uploadDir);
                this.ftpconnection.storeFile(remoteFileName, inputStream);
            }
        } catch (IOException e) {
            throw new Exception("Error al subir la imagen al servidor FTP.", e);
        }
    }




    @Override
    public void downloadFileFromFTP(String ftpRelativePath, String copytoPath)  throws FTPErrors {

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(copytoPath);
        } catch (FileNotFoundException e) {
            ErrorMessage errorMessage = new ErrorMessage(-6, "No se pudo obtener la referencia a la carpeta relativa donde guardar, verifique la ruta y los permisos.");
            logger.error(errorMessage.toString());
            throw new FTPErrors(errorMessage);
        }

        try {
            this.ftpconnection.retrieveFile(ftpRelativePath, fos);
        } catch (IOException e) {
            ErrorMessage errorMessage = new ErrorMessage(-7, "No se pudo descargar el archivo.");
            logger.error(errorMessage.toString());
            throw new FTPErrors(errorMessage);
        }
    }

    @Override
    public void disconnectFTP() throws FTPErrors{
        if (this.ftpconnection.isConnected()) {
            try {
                this.ftpconnection.logout();
                this.ftpconnection.disconnect();
            } catch (IOException f) {
                throw new FTPErrors( new ErrorMessage(-8, "Ha ocurrido un error al realizar la desconexión del servidor FTP"));
            }
        }
    }




}
