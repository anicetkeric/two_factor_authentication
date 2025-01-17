package com.bootlabs.utils.totp;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;

/**
 * Implementation of TOTP: Time-based One-time Password Algorithm
 *
 * @author thoeger
 */
public final class TOTP {
	
	private TOTP() {
		// private utility class constructor
	}

	/**
	 * @param key - secret credential key (HEX)
	 * @return the OTP
	 */
	public static String getOTP(String key) {
		return TOTP.getOTP(TOTP.getStep(), key);
	}

	/**
	 * @param key - secret credential key (HEX)
	 * @param otp - OTP to validate
	 * @return valid?
	 */
	public static boolean validate(final String key, final String otp) {
		return TOTP.validate(TOTP.getStep(), key, otp);
	}

	private static boolean validate(final long step, final String key, final String otp) {
		return TOTP.getOTP(step, key).equals(otp) || (step > 0 && TOTP.getOTP(step - 1, key).equals(otp));
	}

	private static long getStep() {
		// 30 seconds StepSize (ID TOTP)
		return System.currentTimeMillis() / 30000;
	}

	private static String getOTP(final long step, final String key) {
		if (step < 0) {
			throw new IllegalArgumentException("Step must be greater than or equal to zero.");
		}
		StringBuilder steps = new StringBuilder(Long.toHexString(step).toUpperCase());
		while (steps.length() < 16) {
			steps.insert(0, "0");
		}

		// Get the HEX in a Byte[]
		final byte[] msg = TOTP.hexStr2Bytes(steps.toString());
		final byte[] k = TOTP.hexStr2Bytes(key);

		final byte[] hash = hmacSha1(k, msg);

		// put selected bytes into result int
		final int offset = hash[hash.length - 1] & 0xf;
		final int binary = ((hash[offset] & 0x7f) << 24) | ((hash[offset + 1] & 0xff) << 16) | ((hash[offset + 2] & 0xff) << 8) | (hash[offset + 3] & 0xff);
		final int otp = binary % 1000000;

		StringBuilder result = new StringBuilder(Integer.toString(otp));
		while (result.length() < 6) {
			result.insert(0, "0");
		}
		return result.toString();
	}

	/**
	 * This method converts HEX string to Byte[]
	 *
	 * @param hex the HEX string
	 *
	 * @return A byte array
	 */
	private static byte[] hexStr2Bytes(final String hex) {
		// Adding one byte to get the right conversion
		// values starting with "0" can be converted
		final byte[] bArray = new BigInteger("10" + hex, 16).toByteArray();
		final byte[] ret = new byte[bArray.length - 1];

		// Copy all the REAL bytes, not the "first"
		System.arraycopy(bArray, 1, ret, 0, ret.length);
		return ret;
	}

	/**
	 * This method uses the JCE to provide the crypto algorithm. HMAC computes a Hashed Message Authentication Code with the crypto hash
	 * algorithm as a parameter.
	 *
	 * @param keyBytes the bytes to use for the HMAC key
	 * @param text the message or text to be authenticated.
	 */
	private static byte[] hmacSha1(final byte[] keyBytes, final byte[] text) {
		try {
			final Mac hmac = Mac.getInstance("HmacSHA1");
			final SecretKeySpec macKey = new SecretKeySpec(keyBytes, "RAW");
			hmac.init(macKey);
			return hmac.doFinal(text);
		} catch (final GeneralSecurityException gse) {
			throw new UndeclaredThrowableException(gse);
		}
	}

}
