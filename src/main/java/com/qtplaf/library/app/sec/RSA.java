/*
 * Copyright (C) 2015 Miquel Sas
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.qtplaf.library.app.sec;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.qtplaf.library.util.NumberUtils;

/**
 * RSA Implementation.
 * 
 * @author Miquel Sas
 */
public class RSA {

	/**
	 * Generated large prime number (p) using <code>BigInteger.probablePrime(512, new Random())</code>.
	 */
	public static final BigInteger GENERATED_P =
		new BigInteger(
			"7509609897572206980324026390592944787412733559310857475669616518743937188408700902659013595742345580515769683608238232324580209512313315968717177288846767");
	/**
	 * Generated large prime number (q) using <code>BigInteger.probablePrime(512, new Random())</code>.
	 */
	public static final BigInteger GENERATED_Q =
		new BigInteger(
			"8831216111408860630217131849110884900075227683862443497299554379229760584987591219512656814307808435420270506826155853003501822388957832468523565133626873");

	/**
	 * Zero: big integer, widely used.
	 */
	private static final BigInteger ZERO = new BigInteger("0");
	/**
	 * One: big integer, widely used.
	 */
	private static final BigInteger ONE = new BigInteger("1");
	/**
	 * Two: big integer, widely used.
	 */
	private static final BigInteger TWO = new BigInteger("2");
	/**
	 * Bytes of the incoming text will be groupped in chunks of <code>groupBytesSize</code>. Can not exceed 127.
	 */
	private static final int GROUP_BYTES_SIZE = 127;

	/**
	 * The public and private key module.
	 */
	private BigInteger n = null;
	/**
	 * Public encode exponent.
	 */
	private BigInteger e = null;
	/**
	 * Private encode exponent.
	 */
	private BigInteger d = null;

	/**
	 * Returns the relative prime number, starting at 11 up to the argument. Starting at less prime numbers can induce
	 * easily decriptable keys. The argument is the phi Euler funtion.
	 * 
	 * @param phi The phi Euler funtion to find the relative prime.
	 * @return The relative prime.
	 */
	private static BigInteger primeRelative(BigInteger phi) {
		BigInteger rel = new BigInteger("11");
		while (phi.gcd(rel).compareTo(ONE) != 0)
			rel = rel.add(TWO);
		return rel;
	}

	/**
	 * Calculates the private encode exponent given the phi Euler function and the public encode exponent.
	 * 
	 * @param phi The phi Euler funtion.
	 * @param e The public encode exponent.
	 * @return The private encode exponent.
	 */
	private static BigInteger calculate_d(BigInteger phi, BigInteger e) {
		BigInteger x, y, x1, x2, y1, y2, temp, r, orig_phi;
		orig_phi = phi;
		x2 = new BigInteger("1");
		x1 = new BigInteger("0");
		y2 = new BigInteger("0");
		y1 = new BigInteger("1");
		while (e.compareTo(ZERO) > 0) {
			temp = phi.divide(e);
			r = phi.subtract(temp.multiply(e));
			x = x2.subtract(temp.multiply(x1));
			y = y2.subtract(temp.multiply(y1));
			phi = e;
			e = r;
			x2 = x1;
			x1 = x;
			y2 = y1;
			y1 = y;
			if (phi.compareTo(ONE) == 0) {
				y2 = y2.add(orig_phi);
				break;
			}
		}
		return y2;
	}

	/**
	 * Encrypts a decrypted value.
	 * 
	 * @param n The key module.
	 * @param e The public encode exponent.
	 * @param dec The decrypted value to encrypt.
	 * @return The encrypted value.
	 */
	private static BigInteger encrypt(BigInteger n, BigInteger e, BigInteger dec) {
		BigInteger i = new BigInteger("0");
		BigInteger prod = new BigInteger("1");
		BigInteger rem_mod = new BigInteger("0");
		while (e.compareTo(ZERO) > 0) {
			BigInteger r = e.mod(TWO);
			if (i.compareTo(ZERO) == 0) {
				rem_mod = dec.mod(n);
			} else {
				rem_mod = rem_mod.pow(2).mod(n);
			}
			i = i.add(ONE);
			if (r.compareTo(ONE) == 0) {
				prod = prod.multiply(rem_mod).mod(n);
			}
			e = e.divide(TWO);
		}
		return prod;
	}

	/**
	 * Decrypts an encrypted value.
	 * 
	 * @param n The key module.
	 * @param d The private encode exponent.
	 * @param enc The encrypted value to decrypt.
	 * @return The decrypted value.
	 */
	private static BigInteger decrypt(BigInteger n, BigInteger d, BigInteger enc) {
		BigInteger i = new BigInteger("0");
		BigInteger prod = new BigInteger("1");
		BigInteger rem_mod = new BigInteger("0");
		while (d.compareTo(ZERO) > 0) {
			BigInteger r = d.mod(TWO);
			if (i.compareTo(ZERO) == 0) {
				rem_mod = enc.mod(n);
			} else {
				rem_mod = rem_mod.pow(2).mod(n);
			}
			i = i.add(ONE);
			if (r.compareTo(ONE) == 0) {
				prod = prod.multiply(rem_mod).mod(n);
			}
			d = d.divide(TWO);
		}
		return prod;
	}

	/**
	 * Default constructor using a generated key module and phi Euler function (Xavier Valls dixit).
	 */
	public RSA() {
		super();
		setKeyModuleAndPhi(GENERATED_P, GENERATED_Q);
	}

	/**
	 * Constructs a new RSA assing two large prime numbers.
	 * 
	 * @param p A large prime number.
	 * @param q A large prime number.
	 */
	public RSA(BigInteger p, BigInteger q) {
		setKeyModuleAndPhi(p, q);
	}

	/**
	 * Returns the encrypted string in hex form.
	 * 
	 * @param decryptedString The decripted string.
	 * @return The encripted string.
	 */
	public String encrypt(String decryptedString) {
		byte[] encrypted = encrypt(decryptedString.getBytes());
		StringBuilder b = new StringBuilder();
		for (byte enc : encrypted) {
			b.append(NumberUtils.toHexString(enc));
		}
		return b.toString();
	}

	/**
	 * Returns the decrypted string from an encripted hex form.
	 * 
	 * @param encryptedString The encrypted hex form.
	 * @return The decrypted string.
	 */
	public String decrypt(String encryptedString) {
		if (!NumberUtils.isEven(encryptedString.length())) {
			throw new IllegalArgumentException("The length of the encrypted hex string must be even.");
		}
		byte[] encrypted = new byte[encryptedString.length() / 2];
		int cursor = 0;
		for (int i = 0; i < encrypted.length; i++) {
			String b = encryptedString.substring(cursor, cursor + 2);
			encrypted[i] = NumberUtils.parseByte(b);
			cursor += 2;
		}
		return new String(decrypt(encrypted));
	}

	/**
	 * Encrypts an array of bytes.
	 * 
	 * @param decryptedBytes The decrypted array to encrypt.
	 * @return The encrypted array of bytes.
	 */
	public byte[] encrypt(byte[] decryptedBytes) {

		List<Byte> bytes = new ArrayList<>();
		List<BigInteger> numbers = groupByteArray(decryptedBytes);

		for (BigInteger dec : numbers) {
			BigInteger enc = encrypt(n, e, dec);
			byte[] bytesEncoded = enc.toByteArray();
			int length = bytesEncoded.length;
			if (length > 255) {
				throw new IllegalStateException("Fatal Error while encrypting the bytes.");
			}
			bytes.add(new Byte((byte) length));
			for (int j = 0; j < bytesEncoded.length; j++) {
				bytes.add(new Byte(bytesEncoded[j]));
			}
		}

		byte[] encryptedBytes = new byte[bytes.size()];
		for (int i = 0; i < bytes.size(); i++) {
			encryptedBytes[i] = bytes.get(i);
		}
		return encryptedBytes;
	}

	/**
	 * Returns a list of <code>BigInteger</code> by groupping the source byte array into chunks of
	 * <code>groupBySize</code>.
	 * 
	 * @param bytes The source byte array
	 * @return The list of numbers.
	 */
	private List<BigInteger> groupByteArray(byte[] bytes) {
		List<BigInteger> numbers = new ArrayList<>();
		int cursor = 0;
		while (cursor < bytes.length) {
			byte[] bigIntegerBytes = new byte[Math.min(GROUP_BYTES_SIZE, bytes.length - cursor)];
			for (int i = 0; i < bigIntegerBytes.length; i++) {
				bigIntegerBytes[i] = bytes[cursor];
				cursor++;
			}
			numbers.add(new BigInteger(bigIntegerBytes));
		}
		return numbers;
	}

	/**
	 * Decrypts an array of bytes.
	 * 
	 * @param encryptedBytes The encrypted array to decrypt.
	 * @return The decrypted array of bytes.
	 */
	public byte[] decrypt(byte[] encryptedBytes) {

		List<Byte> bytes = new ArrayList<>();
		List<BigInteger> numbers = parseByteArray(encryptedBytes);

		for (BigInteger enc : numbers) {
			BigInteger dec = decrypt(n, d, enc);
			byte[] bytesDec = dec.toByteArray();
			for (int i = 0; i < bytesDec.length; i++) {
				bytes.add(new Byte(bytesDec[i]));
			}
		}

		byte[] decryptedBytes = new byte[bytes.size()];
		for (int i = 0; i < bytes.size(); i++) {
			decryptedBytes[i] = bytes.get(i);
		}
		return decryptedBytes;
	}

	/**
	 * Returns a list of <code>BigInteger</code> by parsing the source byte array that contains chunks of
	 * <code>groupBySize</code> with a length byte preciding each chunk.
	 * 
	 * @param bytes The source byte array
	 * @return The list of numbers.
	 */
	private List<BigInteger> parseByteArray(byte[] bytes) {
		List<BigInteger> numbers = new ArrayList<>();
		int cursor = 0;
		while (cursor < bytes.length) {
			int length = Byte.toUnsignedInt(bytes[cursor]);
			if (length <= 0) {
				throw new IllegalStateException("Invalid format in encrypted text.");
			}
			byte[] bigIntegerBytes = new byte[length];
			for (int i = 0; i < bigIntegerBytes.length; i++) {
				bigIntegerBytes[i] = bytes[cursor + i + 1];
			}
			numbers.add(new BigInteger(bigIntegerBytes));
			cursor += length + 1;
		}
		return numbers;
	}

	/**
	 * Sets the key module and the phi Euler function based on two large prime numbers.
	 * 
	 * @param p A large prime number.
	 * @param q A large prime number.
	 */
	public void setKeyModuleAndPhi(BigInteger p, BigInteger q) {
		// Calculate the key module.
		BigInteger n = p.multiply(q);
		if (n.bitLength() < 512) {
			throw new IllegalStateException("The key module must have a mininum of 512 bits: " + n.bitLength());
		}
		this.n = n;
		// Calculate the phi Euler function.
		BigInteger phi = p.subtract(ONE).multiply(q.subtract(ONE));
		// Calculate the public encode exponent.
		this.e = primeRelative(phi);
		// Calculate the private encode exponent.
		this.d = calculate_d(phi, e);
	}
}
