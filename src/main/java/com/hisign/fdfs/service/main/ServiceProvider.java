package com.hisign.fdfs.service.main;

import com.hisign.fdfs.common.FdfsConstant;
import com.hisign.sdk.config.SysConfigLoader;
import com.hisign.sso.api.constant.UAOPConstant;

/**
 * 服务层启动主类
 * @author chailiangzhi
 * @date 2015-10-13
 * 
 */
public class ServiceProvider {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		initPara();
		com.alibaba.dubbo.container.Main.main(null);
	}

	/**
	 * 
	 */
	private static void initPara() {
		//SysConfigLoader.getInstance().loadSysConfig(UAOPConstant.SYSTEMID);
		//		SysConfigLoader.getInstance().loadSysConfig(OMPConstant.SYSTEMID);
		//SysConfigLoader.getInstance().loadSysConfig(FdfsConstant.SYSTEMID);
		System.setProperty("zkconnect", "127.0.0.1:52181");
		System.setProperty("fdfs_rest_port", "58820");
		
		System.setProperty("fdfs.soTimeout", "1501");
		System.setProperty("fdfs.connectTimeout", "601");
		System.setProperty("fdfs.thumbImage.width", "150");
		System.setProperty("fdfs.thumbImage.height", "150");
		System.setProperty("fdfs.previewImage.width", "800");
		System.setProperty("fdfs.previewImage.height", "800");
		
		System.setProperty("fdfs.trackerList", "172.16.0.112:22122");
		System.setProperty("fast.webServerUrl", "");
	}
}
