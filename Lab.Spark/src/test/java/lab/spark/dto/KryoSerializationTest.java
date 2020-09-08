package lab.spark.dto;

import static org.junit.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.testng.annotations.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoSerializationTest {

	@Test
	public void testFileUpdateloadContentDtoKryoSerialization() throws FileNotFoundException {
		
		Output output = new Output(new FileOutputStream("file.dat"));
		Input input = new Input(new FileInputStream("file.dat"));
		    
		Kryo kryo = new Kryo();
		kryo.register(FileUploadContentDTO.class);
		
		FileUploadContentDTO fileUploadContentDTO = new FileUploadContentDTO();
		fileUploadContentDTO.setFileContent("Hello world");
		fileUploadContentDTO.setFileName("This is the file name");
		fileUploadContentDTO.setSentences(new String[] {"abc","def","hig"});
		
		kryo.writeClassAndObject(output, fileUploadContentDTO);
		output.close();
		
		FileUploadContentDTO fileUploadContentDTO2 = 
				(FileUploadContentDTO) kryo.readClassAndObject(input);
		input.close();
		assertNotNull(fileUploadContentDTO2);
	}
}
