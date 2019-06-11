package com.spier.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;

import org.springframework.util.StringUtils;


/**
 * RSA工具类
 * @author GHB
 * @version 1.0
 * @date 2018.10.23
 */
public class RSAUtils {
	private static final String M_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
	
	// 秘钥大小
	private static final int M_KEY_SIZE = 1024;
	// 最大解密块大小
	private static final int M_MAX_DECRYPT_BLOCK = 128;
	// 最大加密块大小
	private static final int M_MAX_ENCRYPT_BLOCK = 117;
	
	/**
	 * 由字符串生成公钥
	 * @param b64Str 使用base64编码的公钥字符串
	 * @return 可能为null
	 */
	public static RSAPublicKey fromStrPub(String b64Str) {
		if(StringUtils.isEmpty(b64Str)) {
			Logger.getAnonymousLogger().log(Level.WARNING, "b64Str为空，无法生成RSA公钥！");
			return null;
		}
		
		RSAPublicKey res = null;
		
		byte[] keyData = Base64Util.base64Decode(b64Str.getBytes());
		if(null == keyData || keyData.length == 0) {
			Logger.getAnonymousLogger().log(Level.WARNING, "b64解码失败，无法生成RSA公钥！");
			return null;
		}
		
		try {
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyData);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			res = (RSAPublicKey) keyFactory.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	/**
	 * 由字符串生成私钥
	 * @param b64Str
	 * @return 可能为null
	 */
	public static RSAPrivateKey fromStrPriv(String b64Str) {
		if(StringUtils.isEmpty(b64Str)) {
			Logger.getAnonymousLogger().log(Level.WARNING, "b64Str为空，无法生成RSA私钥！");
			return null;
		}
		
		RSAPrivateKey res = null;
		
		byte[] keyData = Base64Util.base64Decode(b64Str.getBytes());
		if(null == keyData || keyData.length == 0) {
			Logger.getAnonymousLogger().log(Level.WARNING, "b64解码失败，无法生成RSA私钥！");
			return null;
		}
		
		try {
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyData);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			res = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	/**
	 * 生成RSA公钥字符串
	 * @param key
	 * @return 可能为null
	 */
	public static String toStr(RSAPublicKey key) {
		if(null == key) {
			return null;
		}
		
		byte[] encPubK = Base64Util.bas64Encode(key.getEncoded());
        if(null == encPubK || encPubK.length == 0) {
        	Logger.getAnonymousLogger().log(Level.WARNING, "b64编码后pub为空，生成失败！");
        	return null;
        }
        
        return new String(encPubK);
	}
	
	/**
	 * 生成RSA私钥字符串
	 * @param key
	 * @return 可能为null
	 */
	public static String toStr(RSAPrivateKey key) {
		if(null == key) {
			return null;
		}
		
		byte[] encPrivK = Base64Util.bas64Encode(key.getEncoded());
        if(encPrivK == null || encPrivK.length == 0) {
        	Logger.getAnonymousLogger().log(Level.WARNING, "b64编码后priv为空，生成失败！");
        	return null;
        }
        
        return new String(encPrivK);
	}
	
	/**
	 * 生成RSA公钥--私钥对
	 * @return 公钥--私钥。可能为空，不为null
	 */
	public static List<String> generateRSAKeyPairStr() {
		List<String> res = new ArrayList<String>();
		
		List<Object> pair = generateRSAKeyPairObj();
		if(pair.size() != 2) {
			Logger.getAnonymousLogger().log(Level.WARNING, "RSA密钥对生成失败！");
			return res;
		}
		
		RSAPublicKey publicKey = (RSAPublicKey) pair.get(0);
		RSAPrivateKey privateKey = (RSAPrivateKey) pair.get(1);
		
		String pubStr = toStr(publicKey);
		if(StringUtils.isEmpty(pubStr)) {
			Logger.getAnonymousLogger().log(Level.WARNING, "公钥转换字符串失败！");
        	return res;
        }
        
        res.add(pubStr);
        
        String privStr = toStr(privateKey);
        if(StringUtils.isEmpty(privateKey)) {
        	Logger.getAnonymousLogger().log(Level.WARNING, "私钥转换字符串失败！");
        	res.clear();
        	return res;
        }
        
        res.add(privStr);
        
		return res;
	}
	
	// 生成RSA公钥-私钥对
	// 返回值为长度为2个列表，其他长度为无效返回
	private static List<Object> generateRSAKeyPairObj() {
		List<Object> res = new ArrayList<Object>();
		
		KeyPairGenerator keyPairGen;
		try {
			keyPairGen = KeyPairGenerator.getInstance("RSA");
			//这个值关系到块加密的大小，可以更改，但是不要太大，否则效率会低
	        keyPairGen.initialize(M_KEY_SIZE);
	        KeyPair keyPair = keyPairGen.generateKeyPair();
	        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
	        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
	        
	        res.add(publicKey);
	        res.add(privateKey);
	        
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	/**
	 * 用公有密钥字符串加密
	 * @param data
	 * @param pubKeyStr，公钥字符串，经过base64编码
	 * @return 可能为null
	 */
	public static byte[] encryptWithPubKey(byte[] data, String pubKeyB64Str) {
		RSAPublicKey key = fromStrPub(pubKeyB64Str);
		if(null == key) {
			Logger.getAnonymousLogger().log(Level.WARNING, "no pubkey generated, abort encrypting");
			return null;
		}
		
		return encrypt(data, key);
	}
	
	/**
	 * 用私有秘钥字符串加密
	 * @param data
	 * @param privKeyB64Str 私钥字符串，经过base64编码
	 * @return 可能为null
	 */
	public static byte[] encryptWithPrivKey(byte[] data, String privKeyB64Str) {
		RSAPrivateKey key = fromStrPriv(privKeyB64Str);
		if(null == key) {
			Logger.getAnonymousLogger().log(Level.WARNING, "no privkey generated, abort encryption.");
			return null;
		}
		
		return encrypt(data, key);
	}
	
//	private static final int M_BLOCK_SIZE = 127;
	
	/**
	 * RSA公钥加密
	 * @param data 要加密的数据
	 * @param pubKey 公钥
	 * @return 密文，可能为null
	 */
	public static byte[] encrypt(byte[] data, RSAPublicKey pubKey) {
		if(null == pubKey) {
			Logger.getAnonymousLogger().log(Level.WARNING, "公钥为null，无法加密");
			return null;
		}
		
		byte[] res = null;
		
		try {
			Cipher cipher = Cipher.getInstance(M_TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			
			//获得加密块大小，如:加密前数据为128个byte，而key_size=1024 加密块大小为127 byte,加密后为128个byte;
            //因此共有2个加密块，第一个127 byte第二个为1个byte
            int blockSize = cipher.getBlockSize();
            if(blockSize <= 0) {
            	blockSize = M_MAX_ENCRYPT_BLOCK;
            }
            
            int outputSize = cipher.getOutputSize(data.length);//获得加密块加密后块大小
            int leavedSize = data.length % blockSize;
            int blocksSize = leavedSize != 0 ? data.length / blockSize + 1 : data.length / blockSize;
            res = new byte[outputSize * blocksSize];
            int i = 0;
            while (data.length - i * blockSize > 0) {
                if (data.length - i * blockSize > blockSize) {
                    cipher.doFinal(data, i * blockSize, blockSize, res, i * outputSize);
                } else {
                    cipher.doFinal(data, i * blockSize, data.length - i * blockSize, res, i * outputSize);
                }
                
                //这里面doUpdate方法不可用，查看源代码后发现每次doUpdate后并没有什么实际动作除了把byte[]放到ByteArrayOutputStream中
                //，而最后doFinal的时候才将所有的byte[]进行加密，可是到了此时加密块大小很可能已经超出了OutputSize所以只好用dofinal方法。
                i++;
            }

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ShortBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		return res;
	}
	
	/**
	 * 用私钥加密
	 * @param data
	 * @param key
	 * @return 可能为null
	 */
	public static byte[] encrypt(byte[] data, RSAPrivateKey key) {
		if(null == key) {
			Logger.getAnonymousLogger().log(Level.WARNING, "私钥为null，无法加密");
			return null;
		}
		
		byte[] res = null;
		
		try {
			Cipher cipher = Cipher.getInstance(M_TRANSFORMATION);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			
			//获得加密块大小，如:加密前数据为128个byte，而key_size=1024 加密块大小为127 byte,加密后为128个byte;
            //因此共有2个加密块，第一个127 byte第二个为1个byte
            int blockSize = cipher.getBlockSize();
            if(blockSize == 0) {
            	blockSize = M_MAX_ENCRYPT_BLOCK;
            }
//            Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("私钥加密块大小：{0}", blockSize));
            int outputSize = cipher.getOutputSize(data.length);//获得加密块加密后块大小
            int leavedSize = data.length % blockSize;
            int blocksSize = leavedSize != 0 ? data.length / blockSize + 1 : data.length / blockSize;
            res = new byte[outputSize * blocksSize];
            int i = 0;
            while (data.length - i * blockSize > 0) {
                if (data.length - i * blockSize > blockSize) {
                    cipher.doFinal(data, i * blockSize, blockSize, res, i * outputSize);
                } else {
                    cipher.doFinal(data, i * blockSize, data.length - i * blockSize, res, i * outputSize);
                }
                
                //这里面doUpdate方法不可用，查看源代码后发现每次doUpdate后并没有什么实际动作除了把byte[]放到ByteArrayOutputStream中
                //，而最后doFinal的时候才将所有的byte[]进行加密，可是到了此时加密块大小很可能已经超出了OutputSize所以只好用dofinal方法。
                i++;
            }

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ShortBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		return res;
	}
	
	/**
	 * 用公钥字符串解密，公钥字符串用base64编码过的
	 * @param data
	 * @param pubKStr
	 * @return 可能为null
	 */
	public static byte[] decryptWithPubK(byte[] data, String pubKStr) {
		RSAPublicKey key = fromStrPub(pubKStr);
		if(key == null) {
			Logger.getAnonymousLogger().log(Level.WARNING, "no pubkey generated, cannot decrypt.");
			return null;
		}
		
		return decrypt(data, key);
	}
	
	/**
	 * 用公钥解密
	 * @param data
	 * @param key
	 * @return 可能为null
	 */
	public static byte[] decrypt(byte[] data, RSAPublicKey key) {
		byte[] res = null;
		
		if(null == key) {
			Logger.getAnonymousLogger().log(Level.WARNING, "key为null，解密失败！");
			return res;
		}
		
		ByteArrayOutputStream bout = null;
		
		try {
			Cipher cipher = Cipher.getInstance(M_TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, key);
//	        int blockSize = cipher.getBlockSize();
	        int blockSize = M_MAX_DECRYPT_BLOCK;
	        
	        bout = new ByteArrayOutputStream();
            int j = 0;
            while (data.length - j * blockSize > 0) {
                bout.write(cipher.doFinal(data, j * blockSize, blockSize));
                j++;
            }
            
            res = bout.toByteArray();

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(null != bout) {
				try {
					bout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
        
		return res;
	}
	
	/**
	 * RSA私钥解密
	 * @param data 要解密的数据
	 * @param key 私钥
	 * @return 解密后的数据，可能为null
	 */
	public static byte[] decrypt(byte[] data, RSAPrivateKey key) {
		byte[] res = null;
		
		if(null == key) {
			Logger.getAnonymousLogger().log(Level.WARNING, "key为null，解密失败！");
			return res;
		}
		
		try {
			Cipher cipher = Cipher.getInstance(M_TRANSFORMATION);
			cipher.init(Cipher.DECRYPT_MODE, key);
//	        int blockSize = cipher.getBlockSize();
			int blockSize = M_MAX_DECRYPT_BLOCK;
	        Logger.getAnonymousLogger().log(Level.INFO, MessageFormat.format("block_size: {0}", blockSize));
	        
	        ByteArrayOutputStream bout = new ByteArrayOutputStream(64);
            int j = 0;
            while (data.length - j * blockSize > 0) {
                bout.write(cipher.doFinal(data, j * blockSize, blockSize));
                j++;
            }
            
            res = bout.toByteArray();

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		return res;
	}
	
	/**
	 * 用base64编码过的私钥解密数据
	 * @param data 要解密的数据
	 * @param privKeyB64Str 经过base64编码过的私钥字符串
	 * @return 可能为null
	 */
	public static byte[] decryptWithPrivKeyStr(byte[] data, String privKeyB64Str) {
		RSAPrivateKey privK = fromStrPriv(privKeyB64Str);
		if(null == privK) {
			return null;
		}
		
		return decrypt(data, privK);
	}
}
