package com.hisign.fdfs.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.github.tobato.fastdfs.domain.MateData;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.exception.FdfsServerException;
import com.github.tobato.fastdfs.proto.ErrorCodeConstants;
import com.hisign.fdfs.api.entity.FdfsFileInfo;
import com.hisign.fdfs.api.service.FileService;
import com.hisign.fdfs.frame.service.CustFastFileStorageClient;

/**
 * 文件服务器接口
 * 封装fastdfs上传文件的API提供HTTP接口，
 * 下载文件由nginx提供HTTP接口
 * @author chailiangzhi
 * @date 2016-11-21
 * 
 */
@Path("file")
@Produces({ MediaType.APPLICATION_JSON })
@Service("fileService")
public class FastDfsServiceImpl implements FileService {

	/**
	 * 
	 */
	private final Logger logger = LoggerFactory.getLogger(getClass());
	/** 支持的图片类型 */
	private static final String[] SUPPORT_IMAGE_TYPE = { "JPG", "JPEG", "PNG", "GIF", "BMP", "WBMP" };
	private static final List<String> SUPPORT_IMAGE_LIST = Arrays.asList(SUPPORT_IMAGE_TYPE);

	/**
	 * 
	 */
	@Autowired()
	@Qualifier("custStorageClient")
	private CustFastFileStorageClient custStorageClient;

	/* (non-Javadoc)
	 * @see com.hisign.sso.api.service.external.FileService#uploadFile(org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput)
	 */
	@Override
	@POST
	@Path("upload")
	@Consumes("multipart/form-data")
	public Map<String, Object> uploadFile(MultipartFormDataInput input) {
		Map<String, List<InputPart>> formParts = input.getFormDataMap();
		List<InputPart> inPart = formParts.get("file");
		Map<String, Object> retMap = new HashMap<String, Object>();
		String flag = "0";
		String message = "失败";
		if (inPart == null) {
			message = "未获取到文件";
			logger.error("未获取到文件");
			//			throw new RestBusinessException(Status.NOT_ACCEPTABLE, "未获取到文件");
		}
		for (InputPart inputPart : inPart) {
			InputStream istream = null;
			try {
				// Retrieve headers, read the Content-Disposition header to obtain the original name of the file
				MultivaluedMap<String, String> headers = inputPart.getHeaders();
				String fileName = parseFileName(headers);
				if (StringUtils.isEmpty(fileName)) {
					message = "文件名为空";
					logger.error("文件名为空");
					//					throw new RestBusinessException(Status.NOT_ACCEPTABLE, "文件名为空");
				}
				// Handle the body of that part with an InputStream
				istream = inputPart.getBody(InputStream.class, null);
				FdfsFileInfo fdfsFileInfo = saveFile(istream, fileName);
				if (fdfsFileInfo != null) {
					flag = "1";
					message = "成功";
					retMap.put("data", fdfsFileInfo);
				} else {
					message = "文件保存失败";
					logger.error("文件保存失败");
					//					throw new RestBusinessException(Status.NOT_ACCEPTABLE, "文件保存失败");
				}

			} catch (Exception e) {
				message = "文件保存异常";
				logger.error("文件保存异常", e);
				//				throw new RestBusinessException(Status.NOT_ACCEPTABLE, "文件保存异常");
			} finally {
				if (istream != null) {
					try {
						istream.close();
					} catch (IOException e) {
						logger.error(e.toString());
					}
				}
			}
		}
		retMap.put("message", message);
		retMap.put("flag", flag);
		return retMap;
	}

	/**
	 * Parse Content-Disposition header to get the original file name
	 * @param headers
	 * @return
	 */
	private String parseFileName(MultivaluedMap<String, String> headers) {
		String[] contentDispositionHeader = headers.getFirst("Content-Disposition").split(";");
		for (String name : contentDispositionHeader) {
			if (name.trim().startsWith("filename")) {
				try {
					name = URLDecoder.decode(name, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					logger.error("编码不支持", e);
				}
				String[] tmp = name.split("=");
				String fileName = tmp[1].trim().replaceAll("\"", "");
				return fileName;
			}
		}
		return "";
	}

	/**
	 * save uploaded file to a defined location on the server
	 * @param uploadedInputStream
	 * @param fileName
	 */
	private FdfsFileInfo saveFile(InputStream uploadedInputStream, String fileName) throws IOException {
		byte[] buff = new byte[1024];
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		int len = uploadedInputStream.read(buff);
		while (len != -1) {
			bout.write(buff, 0, len);
			len = uploadedInputStream.read(buff);
		}
		byte[] fileBuff = bout.toByteArray();//获取内存缓冲中的数据
		long fileSize = fileBuff.length;
		ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBuff);
		String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1);
		try {
			Set<MateData> metaDataSet = null;
			StorePath storePath;
			Boolean isImage = false;
			if (isSupportImage(fileExtName)) {
				isImage = true;
				storePath = custStorageClient.uploadImageAndCrtThumbPreviewImage(inputStream, fileSize, fileExtName,
						metaDataSet);
			} else {
				storePath = custStorageClient.uploadFile(inputStream, fileSize, fileExtName, metaDataSet);
			}
			String groupName = storePath.getGroup();
			String remoteFilename = storePath.getPath();

			logger.info("groupName: " + groupName + ", remoteFilename: " + remoteFilename);

			FdfsFileInfo fInfo = new FdfsFileInfo();
			fInfo.setFileNameLocal(fileName);
			fInfo.setFileNameRemote(remoteFilename);
			fInfo.setIsImage(isImage);
			return fInfo;
		} catch (Exception e) {
			logger.error("保存文件失败", e);
			return null;
		}
	}

	/**
	 * 是否是支持的图片文件
	 * 
	 * @param fileExtName
	 * @return
	 */
	private boolean isSupportImage(String fileExtName) {
		return SUPPORT_IMAGE_LIST.contains(fileExtName.toUpperCase());
	}

	/* (non-Javadoc)
	 * @see com.hisign.fdfs.api.service.FileService#deleteFile(java.lang.String)
	 */
	@Override
	@POST
	@Path("delete")
	@Consumes({ MediaType.APPLICATION_JSON })
	public Map<String, Object> deleteFile(Map<String, String> mapPara) {
		String filePath = mapPara.get("filePath");
		Assert.hasLength(filePath, "filePath必须有值");
		logger.debug("filePath={}", filePath);
		String flag = "0";
		String message;
		int filePathLen = filePath.length();
		if (filePathLen <= 22 || filePathLen >= 128) {
			message = "filePath不合法";
		} else {
			try {
				custStorageClient.deleteFile("group1", filePath);
				flag = "1";
				message = "删除成功";
			} catch (FdfsServerException e) {
				if (e.getErrorCode() == ErrorCodeConstants.ERR_NO_ENOENT) {
					message = e.getMessage();
				} else {
					logger.error("删除失败", e);
					message = "删除失败";
				}
			}
		}
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("message", message);
		retMap.put("flag", flag);
		return retMap;
	}

}
