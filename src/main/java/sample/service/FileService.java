package sample.service;

import lombok.extern.log4j.Log4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.*;

@Log4j
@Service
public class FileService {
	
	static String PUBLICATIONS_PATH = "./content/";

	public static String getPublicationLocalFileName(Long publicationId) {
		return PUBLICATIONS_PATH + publicationId + ".pdf";
	}

	
	public void storeFile(byte[] bytes, Long id) throws IOException {
		String fileName = getPublicationLocalFileName(id);
		this.storeFile(bytes, fileName);
	}

	public void storeFile(byte[] bytes, String fileName) throws IOException {
		
		log.debug(" storing file " + fileName);

		try(BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(fileName)))) {
			stream.write(bytes);
		}
	}
	
	public byte[] getFileAsBytes(Long id) throws IOException {
		String pathName = getPublicationLocalFileName(id);
		return getFileAsBytes(pathName);
	}
	public byte[] getFileAsBytes(String pathName) throws IOException {
		try(InputStream is = new FileInputStream(pathName)) {
			return IOUtils.toByteArray(is);
		}
	}

}
