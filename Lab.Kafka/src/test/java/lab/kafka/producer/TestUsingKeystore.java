package lab.kafka.producer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.testng.annotations.Test;

public class TestUsingKeystore {

	public static final String data = "hello world";

	public static final byte[] clearDataBytes = data.getBytes();

	Cipher ecipher;
	Cipher dcipher;
	
	String charSet = "UTF-8";

	@Test
	public void testEncryptionDecryption() throws KeyStoreException, NoSuchAlgorithmException, CertificateException,
			IOException, UnrecoverableEntryException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, NoSuchProviderException {
		
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		char[] keyStorePassword = "Rsys@Dev1".toCharArray();
		
		try (InputStream keyStoreData = new FileInputStream("C:\\dev\\repos\\git\\keystore\\pem\\keystore")) {
			
			keyStore.load(keyStoreData, keyStorePassword);
			Certificate[] cert = keyStore.getCertificateChain("rsysdev1");
			
			String algorithm = "SHA256withRSA";
			
			for (Certificate c : cert) {
				System.out.println(c.toString());
				algorithm = c.getPublicKey().getAlgorithm();
			}
			Key key = keyStore.getKey("rsysdev1", keyStorePassword);
			
			
			String encryptedData = encrypt(key, data);
		
			System.out.println("Correct decryption" + decrypt(key,encryptedData));

		}
	}

	public String encrypt(Key key, String plainText) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
//		// Key generation for enc and desc
//		KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), salt, iterationCount);
//		SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
//		// Prepare the parameter to the ciphers
//		AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

		// Enc process
		ecipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		ecipher.init(Cipher.ENCRYPT_MODE, key);
		
		byte[] in = plainText.getBytes(charSet);
		byte[] out = ecipher.doFinal(in);
		String encStr = new String(Base64.getEncoder().encode(out));
		return encStr;
	}


	public String decrypt(Key key, String encryptedText) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException,
			UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, IOException, NoSuchProviderException {
		// Key generation for enc and desc
//		KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), salt, iterationCount);
//		SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
//		// Prepare the parameter to the ciphers
//		AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
		// Decryption process; same key will be used for decr
	
		dcipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		dcipher.init(Cipher.DECRYPT_MODE, key);
		byte[] enc = Base64.getDecoder().decode(encryptedText);
		byte[] utf8 = dcipher.doFinal(enc);
		
		String plainStr = new String(utf8, charSet);
		return plainStr;
	}
	
	
}
