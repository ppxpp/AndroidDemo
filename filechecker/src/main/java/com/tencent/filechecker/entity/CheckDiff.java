package com.tencent.filechecker.entity;


public class CheckDiff implements Comparable<CheckDiff>{

	public enum DataType{
		WeCarSpeech,
		SogouInputMethod,
		WeCarNavi,
		WeCarMusic,
		MD5Result;

		public static DataType map(String filePath){
			if (filePath.contains("wecarnavi")){
				return WeCarNavi;
			}else if (filePath.contains("wecarmusic")){
				return WeCarMusic;
			}else if (filePath.contains("wecarspeech")){
				return WeCarSpeech;
			}else if (filePath.contains("sogou")){
				return SogouInputMethod;
			}else{
				return MD5Result;
			}
		}
	}
	
	public enum DiffType{
		Missing,//文件丢失
		Modify,//文件长度一致，MD5校验不一致
		DifSize,//文件长度不一致
	}
	
	public DiffType diffType;
	public DataType dataType;
	public String filePath;
	public String md5Stander;
	public String md5Calculated;
	
	//public long lastModified;
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("dataType = " + dataType
				+ ", diffType = " + diffType
				+ ", filePath = " + filePath 
				+ ", md5Stander = " + md5Stander 
				+ ", md5Calculated = " + md5Calculated 
				//+ ", lastModify = " + lastModified
				//+ "(" + TimeHelper.unixTimeToStr(lastModified / 1000) + ")"
				);
		return sb.toString();
	}

	@Override
	public int compareTo(CheckDiff another) {
		if (diffType != another.diffType) {
			return diffType.compareTo(another.diffType);
		}
		if (dataType != another.dataType) {
			return dataType.compareTo(another.dataType);
		}
		return filePath.compareTo(another.filePath);
	}
}
