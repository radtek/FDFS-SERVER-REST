/**
 * 
 */
package com.hisign.fdfs.frame.domain;

import com.github.tobato.fastdfs.domain.ThumbImageConfig;

/**
 * @author chailiangzhi
 * @date 2016-12-14
 * 
 */
public interface ThumbPreviewImageConfig extends ThumbImageConfig {
	/**
	 * 获得预览图宽
	 * 
	 * @return
	 */
	int getPreviewWidth();

	/**
	 * 获得预览图高
	 * 
	 * @return
	 */
	int getPreviewHeight();

	/**
	 * 获得预览图前缀
	 * 
	 * @param path
	 * @return
	 */
	String getPreviewPrefixName();

	/**
	 * 获得预览图路径
	 * 
	 * @param masterFilename
	 * @return
	 */
	String getPreviewImagePath(String masterFilename);
}
