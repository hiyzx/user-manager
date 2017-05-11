package com.zero.util;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaHelper {
	static final String MEDIA_TOKEN = "#media_host_token#";
	static String MEDIA_HOST;
	static String MEDIA_PATH;
	private static final String REGEX = "^(http|https)://.+";

	public MediaHelper(String mediaPath, String mediaHost) {
		MEDIA_PATH = mediaPath;
		MEDIA_HOST = mediaHost;
	}

	/**
	 * 添加前缀 banner1.jpg -> http://zero.com/banner1.jpg
	 * 
	 * @param path
	 * @return
	 */
	public static String getFullMediaPath(String path) {
		String rtn = null;
		if (StringUtils.hasText(path)) {
			Pattern pattern = Pattern.compile(REGEX);
			Matcher matcher = pattern.matcher(path);
			rtn = matcher.matches() ? path : String.format("%s%s", MEDIA_HOST, path);
		}
		return rtn;
	}

	/**
	 * 添加占位符 http://zero.com/banner1.jpg -> #media_host_token#1.jpg
	 * 
	 * @param source
	 * @return
	 */
	public static String setMediaPathToken(String source) {
		if (StringUtils.hasText(source)) {
			source = source.replaceAll(MEDIA_HOST, MEDIA_TOKEN);
		}
		return source;
	}

	/**
	 * 解析占位符 #media_host_token#1.jpg -> http://ttest.bondwebapp.com/banner1.jpg
	 * 
	 * @param source
	 * @return
	 */
	public static String replaceMediaPathToken(String source) {
		if (StringUtils.hasText(source)) {
			source = source.replaceAll(MEDIA_TOKEN, MEDIA_HOST);
		}
		return source;
	}

	public static String getMEDIA_PATH() {
		return MEDIA_PATH;
	}
}