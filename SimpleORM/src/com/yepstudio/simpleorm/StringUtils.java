package com.yepstudio.simpleorm;

/**
 * 字符串操作类
 * @author zzljob@gmail.com
 * @date 2013-6-6
 *
 */
public class StringUtils {
	
	/**
	 * 将数组拼接成字符串
	 * @param inputArray
	 * @param separator
	 * @return
	 */
	public static String join(String[] inputArray, String separator) {
		String output = "";

		if (inputArray != null && inputArray.length > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(inputArray[0]);

			for (int i = 1; i < inputArray.length; i++) {
				sb.append(separator);
				sb.append(inputArray[i]);
			}

			output = sb.toString();
		}

		return output;
	}
}
