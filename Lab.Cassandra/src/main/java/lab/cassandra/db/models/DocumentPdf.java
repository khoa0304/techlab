package lab.cassandra.db.models;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;

import com.datastax.driver.core.DataType.Name;
import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Table(name = DocumentPdf.TABLE_NAME)
public class DocumentPdf implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "document_pdf";
	
	
	public static final String FILENAME_COL = "fileName";
	public static final String BINARYCONTENT_COL = "binnary_content";
	public static final String FILECONTENT_COL = "fileContent";
	public static final String SIZE_COL = "size";
	
	
	public DocumentPdf() {}
	
	public static enum COLUMNS {
		
		FILE_NAME(FILENAME_COL),
		BINARY_CONTENT(BINARYCONTENT_COL), 
		FILE_CONTENT(FILECONTENT_COL), 
		SIZE(SIZE_COL), 
		UUID("uuid");
		
		private String columnName;
		
		private COLUMNS(String columnName) {
			this.columnName = columnName;
		}
		
		public String getColumnName() {
			return this.columnName;
		}
	}

	@Column(name=FILENAME_COL)
	@PrimaryKey
	@PartitionKey
	@CassandraType(type=Name.VARCHAR)
	private String fileName;

	
//	@Column(name = "binary_content")
//	@ClusteringColumn
//	@CassandraType(type=Name.BLOB)
//	private ByteBuffer binaryContent;
	
	@Column(name = "fileContent")
	@ClusteringColumn
	@CassandraType(type=Name.TEXT)
	private String fileContent;

	@ClusteringColumn(1)
	@CassandraType(type=Name.BIGINT)
	private long size;

	@ClusteringColumn(2)
	@CassandraType(type=Name.UUID)
	private UUID uuid;
	
	// all other column will be used as regular columns

	public DocumentPdf(String fileName, String fileContent, long size) {
		this.fileName = fileName;
		this.fileContent = fileContent;
		this.size = size;
		this.uuid = UUID.randomUUID();
	}

    
	public String getFileName() {
		return fileName;
	}	

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID id) {
		this.uuid = id;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}




//	public ByteBuffer getBinaryContent() {
//		return binaryContent;
//	}
//	public void setBinaryContent(ByteBuffer binaryContent) {
//		this.binaryContent = binaryContent;
//	}

	public String getFileContent() {
		return fileContent;
	}


	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DocumentPdf other = (DocumentPdf) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "DocumentPdf [fileName=" + fileName + ", content=" + fileContent + ", size=" + size + "]";
	}
}
