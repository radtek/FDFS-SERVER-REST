/**
 * 
 */
package com.hisign.fdfs.frame.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.annotation.Resource;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import com.github.tobato.fastdfs.domain.MateData;
import com.github.tobato.fastdfs.domain.StorageNode;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.exception.FdfsUploadImageException;
import com.github.tobato.fastdfs.proto.storage.StorageUploadSlaveFileCommand;
import com.github.tobato.fastdfs.service.DefaultFastFileStorageClient;
import com.hisign.fdfs.frame.domain.ThumbPreviewImageConfig;
import com.hisign.fdfs.frame.service.CustFastFileStorageClient;

/**
 * 自定义面向应用的接口实现
 * @author chailiangzhi
 * @date 2016-12-14
 * 
 */
@Component("custStorageClient")
public class CustFastFileStorageClientImpl extends DefaultFastFileStorageClient implements CustFastFileStorageClient {

	@Resource(name = "thumbImageConfig")
	private ThumbPreviewImageConfig thumbPreviewImageConfig;

	/* (non-Javadoc)
	 * @see com.hisign.fdfs.frame.service.CustFastFileStorageClient#uploadImageAndCrtThumbPreviewImage(java.io.InputStream, long, java.lang.String, java.util.Set)
	 */
	@Override
	public StorePath uploadImageAndCrtThumbPreviewImage(InputStream inputStream, long fileSize, String fileExtName,
			Set<MateData> metaDataSet) {
		byte[] bytes = inputStreamToByte(inputStream);
		ByteArrayInputStream inputStreamNew = new ByteArrayInputStream(bytes);
		StorePath path = uploadImageAndCrtThumbImage(inputStreamNew, fileSize, fileExtName, metaDataSet);

		StorageNode client = trackerClient.getStoreStorage();
		// 上传预览图
		uploadPreviewImage(client, new ByteArrayInputStream(bytes), path.getPath(), fileExtName);
		return path;
	}

	/**
	 * 获取byte流
	 * 
	 * @param inputStream
	 * @return
	 */
	private byte[] inputStreamToByte(InputStream inputStream) {
		try {
			return IOUtils.toByteArray(inputStream);
		} catch (IOException e) {
			LOGGER.error("image inputStream to byte error", e);
			throw new FdfsUploadImageException("upload ThumbImage error", e.getCause());
		}
	}

	/**
	 * 上传预览图
	 * 
	 * @param client
	 * @param inputStream
	 * @param fileSize
	 * @param fileExtName
	 * @param metaDataSet
	 */
	private void uploadPreviewImage(StorageNode client, InputStream inputStream, String masterFilename,
			String fileExtName) {
		ByteArrayInputStream thumbImageStream = null;
		try {
			thumbImageStream = getPreviewImageStream(inputStream);// getFileInputStream
			// 获取文件大小
			long fileSize = thumbImageStream.available();
			// 获取预览图前缀
			String prefixName = thumbPreviewImageConfig.getPreviewPrefixName();
			StorageUploadSlaveFileCommand command = new StorageUploadSlaveFileCommand(thumbImageStream, fileSize,
					masterFilename, prefixName, fileExtName);
			connectionManager.executeFdfsCmd(client.getInetSocketAddress(), command);

		} catch (IOException e) {
			LOGGER.error("upload ThumbImage error", e);
			throw new FdfsUploadImageException("upload ThumbImage error", e.getCause());
		} finally {
			IOUtils.closeQuietly(thumbImageStream);
		}
	}

	/**
	 * 获取预览图
	 * 
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	private ByteArrayInputStream getPreviewImageStream(InputStream inputStream) throws IOException {
		// 在内存当中生成预览图
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		//@formatter:off
		Thumbnails.of(inputStream)
				.size(thumbPreviewImageConfig.getPreviewWidth(), thumbPreviewImageConfig.getPreviewHeight())
				.toOutputStream(out);
		//@formatter:on
		return new ByteArrayInputStream(out.toByteArray());
	}

}
