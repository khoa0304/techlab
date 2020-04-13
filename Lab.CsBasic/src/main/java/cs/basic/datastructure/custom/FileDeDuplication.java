package cs.basic.datastructure.custom;

import java.io.File;

public interface FileDeDuplication {

	/**
	 * <p>This API will remove all duplicated records in the File and return a new File which contains only unique records.</p>
	 * <p> A record is a Row in this file such as CSV which has multiple attributes separated by the same delimiter such as comma. 
	 * All attributes will have the same enclosure such as double quote (""), single quote (''), etc.
	 * Example of comma delimiter and double quote enclosure:  Header Row :"FirstName","LastName", Value Row :"Barack", "Obama"
	 *  </p>
	 * 
	 * 
	 * @param originalFile Original File which might contain duplicated records within this file
	 * @param attributeDelimter Delimiter which separates attributes, both for header and all rows. 
	 * @param enclosure Enclosure which contains the attributes
	 * @param optionalHeader if file doesn't contain header row, header will need to be passed in to this method. 
	 * Otherwise, header from the file will be used
	 * @return New File which only contains unique records
	 */
	File removeDuplicatedRecordsInFile(File originalFile,String attributeDelimter, String enclosure, String optionalHeader);
	
	
	/**
	 * 
	 * <p>This API will remove all duplicated records between the 2 files and return a new File which contains only unique records.</p>
	 * <p> A record is a Row in this file such as CSV which has multiple attributes separated by the common delimiter such as comma. 
	 * All attributes will have the same enclosure such as double quote (""), single quote (''), etc.
	 * Example of comma delimiter and double quote enclosure :  Header Row :"FirstName","LastName", Value Row :"Barack", "Obama"
	 *  </p>
	 * 
	 * For efficiency, this API is expected to be used only after duplicated records have been removed in the 2 files individually, or
	 * internally, this API can be implemented in such a way that it will remove all duplicated records within each file itself first before 
	 * removing duplicated records between file1 and file2
	 * 
	 * @param file1 Expecting file1 doesn't contain any duplicated record within itself
	 * @param file2 Expecting file2 doesn't contain any duplicated record within itself
	 * @param attributeDelimter Delimiter which separates attributes, both for header and all rows; expecting to be the same for both file1 and file2
	 * @param enclosure Enclosure which contains the attributes; expecting to be the same for both file1 and file2
	 * @param optionalHeader if file doesn't contain header row, header will need to be passed in to this method.Otherwise, header from the file will be used
	 * Both file1 and files are expecting to have identical header.
	 * 
	 * @return New File which only contains unique records among file1 and file2
	 */
	File removeDuplicatedRecordsBetweenFiles(File file1, File file2, String attributeDelimter, String enclosure, String optionalHeader);
}
