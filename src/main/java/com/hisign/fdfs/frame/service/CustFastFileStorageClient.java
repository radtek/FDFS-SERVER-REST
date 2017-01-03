/**
 * 
 */
package com.hisign.fdfs.frame.service;

import java.io.InputStream;
import java.util.Set;

import com.github.tobato.fastdfs.domain.MateData;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;

/**
 * @author chailiangzhi
 * @date 2016-12-14
 * 
 */
public interface CustFastFileStorageClient extends FastFileStorageClient {

	/**
	 * 上传图片并且生成缩略图预览图
	 * 
	 * <pre>
	 * 支持的图片格式包括"JPG", "JPEG", "PNG", "GIF", "BMP", "WBMP"
	 * </pre>
	 * 
	 * @param inputStream
	 * @param fileSize
	 * @param fileExtName
	 * @param metaDataSet
	 * @return
	 */
	StorePath uploadImageAndCrtThumbPreviewImage(InputStream inputStream, long fileSize, String fileExtName,
			Set<MateData> metaDataSet);
}
