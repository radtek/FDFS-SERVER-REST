package com.hisign.fdfs.api.entity;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * 文件信息实体
 * @author chailiangzhi
 * @date 2016-11-21
 * 
 */
public class FdfsFileInfo implements java.io.Serializable {

	/**
	*
	*/
	private static final long serialVersionUID = 1L;

	/**
	*
	*/
	@JsonIgnore
	private int id;

	/**
	*
	*/
	private String fileNameLocal;

	/**
	* UrlEncode后的文件名
	*/
	@JsonIgnore
	private String fileNameLocalEnc;

	/**
	*
	*/
	private String fileNameRemote;

	/**
	* 防盗链
	*/
	@JsonIgnore
	private String fileToken;

	/**
	* 是否图片
	*/
	private Boolean isImage;

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFileNameLocal() {
		return this.fileNameLocal;
	}

	public void setFileNameLocal(String fileNameLocal) {
		this.fileNameLocal = fileNameLocal;
	}

	/**
	 * @return the fileNameLocalEnc
	 */
	public String getFileNameLocalEnc() {
		return fileNameLocalEnc;
	}

	/**
	 * @param fileNameLocalEnc the fileNameLocalEnc to set
	 */
	public void setFileNameLocalEnc(String fileNameLocalEnc) {
		this.fileNameLocalEnc = fileNameLocalEnc;
	}

	public String getFileNameRemote() {
		return this.fileNameRemote;
	}

	public void setFileNameRemote(String fileNameRemote) {
		this.fileNameRemote = fileNameRemote;
	}

	/**
	 * @return the fileToken
	 */
	public String getFileToken() {
		return fileToken;
	}

	/**
	 * @param fileToken the fileToken to set
	 */
	public void setFileToken(String fileToken) {
		this.fileToken = fileToken;
	}

	/**
	 * @return the isImage
	 */
	public Boolean getIsImage() {
		return isImage;
	}

	/**
	 * @param isImage the isImage to set
	 */
	public void setIsImage(Boolean isImage) {
		this.isImage = isImage;
	}

}
