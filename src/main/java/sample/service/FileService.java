package sample.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.log4j.Log4j;

@Log4j
@Service
public class FileService {
	
	private static String PUBLICATIONS_PATH = "./";

	public static String getPublicationLocalFileName(Long publicationId) {
		return PUBLICATIONS_PATH + publicationId + ".pdf";
	}

	
	public void storeFile(MultipartFile file, Long id) throws IOException {
		String fileName = getPublicationLocalFileName(id);
		this.storeFile(file, fileName);
	}
	
	public void storeFile(MultipartFile file, String fileName) throws IOException {	
		
		log.debug(" storing file " + fileName);

		byte[] bytes = file.getBytes();
		BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(fileName)));
		stream.write(bytes);
		stream.close();

	}
	
	public byte[] getFileAsBytes(Long id) throws IOException {
		String pathName = getPublicationLocalFileName(id);
		return IOUtils.toByteArray(new FileInputStream(pathName));	
	}

}
