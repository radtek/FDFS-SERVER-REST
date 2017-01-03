/**
 * 
 */
package com.hisign.fdfs.frame.domain;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.github.tobato.fastdfs.domain.DefaultThumbImageConfig;

/**
 * 缩略图和预览图配置参数
 * @author chailiangzhi
 * @date 2016-12-14
 * 
 */
@Component("thumbImageConfig")
public class CustThumbPreviewImageConfig extends DefaultThumbImageConfig implements ThumbPreviewImageConfig {

	private int previewWidth;

	private int previewHeight;

	/* (non-Javadoc)
	 * @see com.github.tobato.fastdfs.domain.ThumbImageConfig#getPrefixName()
	 */
	@Override
	public String getPrefixName() {
		return "_thumb";
	}

	@Value("${fdfs.previewImage.width}")
	public void setPreviewWidth(int previewWidth) {
		this.previewWidth = previewWidth;
	}

	@Value("${fdfs.previewImage.height}")
	public void setPreviewHeight(int previewHeight) {
		this.previewHeight = previewHeight;
	}

	/* (non-Javadoc)
	 * @see com.hisign.fdfs.frame.domain.ThumbPreviewImageConfig#getPreviewWidth()
	 */
	@Override
	public int getPreviewWidth() {
		return previewWidth;
	}

	/* (non-Javadoc)
	 * @see com.hisign.fdfs.frame.domain.ThumbPreviewImageConfig#getPreviewHeight()
	 */
	@Override
	public int getPreviewHeight() {
		return previewHeight;
	}

	/* (non-Javadoc)
	 * @see com.hisign.fdfs.frame.domain.ThumbPreviewImageConfig#getPreviewPrefixName()
	 */
	@Override
	public String getPreviewPrefixName() {
		return "_preview";
	}

	/* (non-Javadoc)
	 * @see com.hisign.fdfs.frame.domain.ThumbPreviewImageConfig#getPreviewImagePath(java.lang.String)
	 */
	@Override
	public String getPreviewImagePath(String masterFilename) {
		Validate.notBlank(masterFilename, "主文件不能为空");
		StringBuilder buff = new StringBuilder(masterFilename);
		int index = buff.lastIndexOf(".");
		buff.insert(index, getPreviewPrefixName());
		return buff.toString();
	}

}
