package com.spier.common.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES工具类
 * @author GHB
 * @version 1.0
 * @date 2018.10.20
 */
public class AESUtil {

	//默认的加密算法
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final int M_KEY_SIZE = 128;

    /**
     * 加密
     * @param data 要加密的数据
     * @param key 密钥
     * @return 加密后的数据，可能为null
     */
    public static byte[] encrypt(byte[] data, byte[] key) {
        byte[] res = null;
        
		try {
			byte[] rawKey = getRawKey(key);
			res = encrypto(data, rawKey);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		
        return res;
    }

    /**
     * 解密
     * @param byteData 要解密的数据
     * @param byteKey 密钥
     * @return 解密后的数据，可能为null
     */
    public static byte[] decrypt(byte[] byteData, byte[] byteKey) {
        byte[] res = null;
		try {
			byte[] rawKey = getRawKey(byteKey);
			Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("rawKey: {0}", Arrays.toString(rawKey)));
			res = decrypto(byteData, rawKey);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		
        return res;
    }


    /***
     * AES加密算法加密
     * @param byteData 数据
     * @param byteKey key
     * @return
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException 
     * @throws Exception
     */
    private static byte[] encrypto(byte[] byteData, byte[] byteKey) 
    		throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, 
    		IllegalBlockSizeException, BadPaddingException {
        return Ase(byteData, byteKey, Cipher.ENCRYPT_MODE);
    }

    /***
     * AES加密算法解密
     * @param byteData 数据
     * @param byteKey key
     * @return
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException 
     * @throws Exception
     */
    private static byte[] decrypto(byte[] byteData, byte[] byteKey) 
    		throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, 
    		IllegalBlockSizeException, BadPaddingException {
        return Ase(byteData, byteKey, Cipher.DECRYPT_MODE);
    }


    /***
     *
     * @param byteData
     * @param byteKey
     * @param opmode
     * @return
     * @throws NoSuchPaddingException 
     * @throws NoSuchAlgorithmException 
     * @throws InvalidKeyException 
     * @throws BadPaddingException 
     * @throws IllegalBlockSizeException 
     * @throws Exception
     */
    private static byte[] Ase(byte[] byteData, byte[] byteKey, int opmode) 
    		throws NoSuchAlgorithmException, NoSuchPaddingException, 
    		InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
        SecretKeySpec skeySpec = new SecretKeySpec(byteKey, "AES");
        cipher.init(opmode, skeySpec);
        return cipher.doFinal(byteData);
    }

    private static byte[] getRawKey(byte[] seed) throws NoSuchAlgorithmException, NoSuchProviderException {
    	SecretKeySpec key = new SecretKeySpec(seed, "AES");
    	return key.getEncoded();
    }

//    public static class CryptoProvider extends Provider {
//        /**
//		 * 
//		 */
//		private static final long serialVersionUID = 6649862610044239734L;
//
//		/**
//         * Creates a Provider and puts parameters
//         */
//        public CryptoProvider() {
//            super("Crypto", 1.0, "HARMONY (SHA1 digest; SecureRandom; SHA1withDSA signature)");
//            put("SecureRandom.SHA1PRNG",
//                    "org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl");
//            put("SecureRandom.SHA1PRNG ImplementedIn", "Software");
//        }
//    }
}
