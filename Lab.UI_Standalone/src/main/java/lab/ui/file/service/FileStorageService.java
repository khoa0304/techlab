package lab.ui.file.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lab.ui.exception.FileNotFoundException;
import lab.ui.exception.FileStorageException;

@Service
public class FileStorageService {

	private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
	private final Path filePath;

	@Autowired
	public FileStorageService(FileStorageProperties fileStorageProperties) {

		this.filePath = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
		
		logger.info("\n\n===================================================================================\n\n");
		
		try {
		
			char ps = File.separatorChar;
			if(ps == '/') {
				Set<PosixFilePermission> permissions = PosixFilePermissions.asFileAttribute(getPosixFilePermission()).value();
				FileAttribute<Set<PosixFilePermission>> fileAttributes = PosixFilePermissions.asFileAttribute(permissions);
				Files.createDirectories(this.filePath, fileAttributes);
			
				logger.info("Finished creating directory for uploaded file {}", this.filePath.toAbsolutePath());
			}
			else{
				File file = this.filePath.toFile();
				file.mkdirs();
				file.setWritable(true);
				file.setReadable(true);
				logger.info("Finished creating directory for uploaded file {}", this.filePath.toAbsolutePath());
			}
			
			logger.info("\n\n===================================================================================\n\n");
			
		} catch (Exception ex) {
			throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
					ex);
		}
	}

	public File storeFile(MultipartFile file) {
		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());

		try {
			// Check if the file's name contains invalid characters
			if (fileName.contains("..")) {
				throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
			}

			// Copy file to the target location (Replacing existing file with the same name)
			Path targetLocation = this.filePath.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			
			return targetLocation.toFile();
		} catch (IOException ex) {
			throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
		}
		finally{
			
			if(file != null) {
				try {
					file.getInputStream().close();
				} catch (IOException e) {
					logger.error("", e);
				}
			}
		}
		
	}

	public Resource loadFileAsResource(String fileName) {
		try {
			Path filePath = this.filePath.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new FileNotFoundException("File not found " + fileName);
			}
		} catch (MalformedURLException ex) {
			throw new FileNotFoundException("File not found " + fileName, ex);
		}
	}

	private Set<PosixFilePermission> getPosixFilePermission() throws IOException {
		// using PosixFilePermission to set file permissions 755
		Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
		// add owners permission
		perms.add(PosixFilePermission.OWNER_READ);
		perms.add(PosixFilePermission.OWNER_WRITE);
		perms.add(PosixFilePermission.OWNER_EXECUTE);
		// add group permissions
		perms.add(PosixFilePermission.GROUP_READ);
		perms.add(PosixFilePermission.GROUP_EXECUTE);
		// add others permissions
		perms.add(PosixFilePermission.OTHERS_READ);
		perms.add(PosixFilePermission.OTHERS_EXECUTE);

		// Files.setPosixFilePermissions(Paths.get(filePath), perms);
		return perms;
	}
}
