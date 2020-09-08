package lab.common.file.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lab.common.file.dto.DocumentDto;

@Service
public class FileResourceUtil {

	private static final Logger logger = LoggerFactory.getLogger(FileResourceUtil.class);

	public static final String TEXT_FILE_EXTENSIOn = ".txt";

	public File writeFileContentAsText(DocumentDto documentDto, String content) throws IOException {

		RandomAccessFile randomAccessFile = null;
		FileChannel rwChannel = null;

		try {

			final File newTextFile = new File(documentDto.getAbsoluteDirectoryPath(),
					documentDto.getFileNamePlusExtesion()+TEXT_FILE_EXTENSIOn);
			randomAccessFile = new RandomAccessFile(newTextFile, "rw");

			rwChannel = randomAccessFile.getChannel();

			byte[] buffer = content.getBytes();
			ByteBuffer wrBuf = rwChannel.map(FileChannel.MapMode.READ_WRITE, 0, buffer.length);
			wrBuf.put(buffer);
			
			return newTextFile;
		} catch (IOException e) {
			logger.error("{}", e);
			throw e;
		}

		finally {

			if (randomAccessFile != null) {
				try {
					
					if (rwChannel != null) {
						rwChannel.close();
					}

					randomAccessFile.close();

				} catch (IOException e) {
					logger.error(e.toString());
				}
			}

		}
	}

	
	public String readFileContentAsText(DocumentDto documentDto) throws IOException {

		FileInputStream stream = null;
		Reader reader = null;
		
		try {

			final File file = new File(documentDto.getAbsoluteDirectoryPath(),
					documentDto.getFileNamePlusExtesion());
			stream = new FileInputStream(file);
			
		    StringBuilder sb = new StringBuilder();
		    reader = new InputStreamReader(stream, "UTF-8");  //or whatever encoding
		    char[] buf = new char[4096];
		    int amt = reader.read(buf);
		    while(amt > 0) {
		        sb.append(buf, 0, amt);
		        amt = reader.read(buf);
		    }
		    
		    final String stringContent = sb.toString();

			return stringContent;
		} catch (IOException e) {
			logger.error("{}", e);
			throw e;
		}

		finally {

			if(reader != null) {
				reader.close();
			}
			
			if (stream != null) {
				try {
					stream.close();

				} catch (IOException e) {
					logger.error(e.toString());
				}
			}
		}
	}
}
