package com.kcm.garage.block;

import java.security.NoSuchAlgorithmException;

import com.kcm.garage.encript.SHA256;


public class BlockUtil {

	public static Block generateGenesisBlock() {
		int index = 0;
		long timestamp = System.currentTimeMillis();
		String previous_hash = "0";
		String hash = "0";
		BlockData data = new BlockData("Kartk-Chandra-mandal", 31);
		return new Block(index, previous_hash, timestamp, hash, data);
	}

	public static String wrapBlockContent(int index, String previous_hash, long timestamp, BlockData data)
			throws NoSuchAlgorithmException {

		String content = index + previous_hash + timestamp + data.toString();
		return SHA256.toSha256(content);
	}

}
