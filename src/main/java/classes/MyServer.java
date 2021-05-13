/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classes;

/**
 *
 * @author Станислав
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * Servlet implementation class MyServlet
 */
@MultipartConfig
@WebServlet("/server")
public class MyServer extends HttpServlet {
	private static final long serialVersionUID = 1L;

        private static final String UPLOAD_DIR = "uploads";
    /**
     * Default constructor. 
     */
    public MyServer() {
        // TODO Auto-generated constructor stub
    }

	/**
         * @param request
         * @param response
         * @throws javax.servlet.ServletException
         * @throws java.io.IOException
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
        @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            // TODO Auto-generated method stub
            // gets absolute path of the web application
            String applicationPath = request.getServletContext().getRealPath("");
            // constructs path of the directory to save uploaded file
            String uploadFilePath = "D:\\TouristCam\\building_keras";

            // creates the save directory if it does not exists
            File fileSaveDir = new File(uploadFilePath);
            if (!fileSaveDir.exists()) {
                fileSaveDir.mkdirs();
            }
            System.out.println("Upload File Directory="+fileSaveDir.getAbsolutePath());

            String fileName = null;
            //Get all the parts from request and write it to the file on server
            for (Part part : request.getParts()) {
                fileName = getFileName(part);
                part.write(uploadFilePath + File.separator + fileName);
            }
            
            //  Запускаем нейронную сеть

            ProcessBuilder processBuilder = new ProcessBuilder("C:\\Users\\Станислав\\AppData\\Local\\Programs\\Python\\Python38\\python.exe", "D:\\TouristCam\\building_keras\\test_model.py");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            
            try {
                Thread.sleep(8000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            

            String answer = new String(Files.readAllBytes(Paths.get(uploadFilePath + File.separator + "out.txt")));
	    PrintWriter printWriter = response.getWriter().append(answer);            

	}
        
         /**
         * Utility method to get file name from HTTP header content-disposition
         */
        private String getFileName(Part part) {
            String contentDisp = part.getHeader("content-disposition");
            System.out.println("content-disposition header= "+contentDisp);
            String[] tokens = contentDisp.split(";");
            for (String token : tokens) {
                if (token.trim().startsWith("filename")) {
                    return token.substring(token.indexOf("=") + 2, token.length()-1);
                }
            }
            return "";
        }

}
