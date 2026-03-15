/**
 * -----------------------------------
 * Copyright (c) 2022-2026
 * All rights reserved, Designed By www.linfengtech.cn
 * 林风社交论坛商业版本请务必保留此注释头信息
 * 商业版授权联系技术客服	 QQ:  3582996245
 * 严禁分享、盗用、转卖源码或非法牟利！
 * 版权所有 ，侵权必究！
 * -----------------------------------
 */
package io.linfeng.modules.app.controller;

import io.linfeng.common.exception.LinfengException;
import io.linfeng.common.utils.Constant;
import io.linfeng.common.utils.FileCheckUtil;
import io.linfeng.common.utils.R;
import io.linfeng.common.utils.Result;
import io.linfeng.modules.oss.factory.OSSFactory;
import io.linfeng.modules.oss.entity.SysOssEntity;
import io.linfeng.modules.oss.service.SysOssService;
import io.linfeng.modules.oss.service.impl.MinioService;
import io.linfeng.modules.sys.service.SysConfigService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * APP文件上传
 *
 */
@RestController
@RequestMapping("app/common")
@Tag(name = "移动端——文件模块")
public class AppOssController {

	@Value("${cloudstorage.max-size}")
	private Long maxSize;

	@Value("${linfeng.path.image}")
	private String imagePath;

	@Value("${linfeng.path.file}")
	private String filePath;

	@Value("${linfeng.path.video}")
	private String videoPath;

	@Autowired
	private SysOssService sysOssService;

	@Autowired
	private SysConfigService sysConfigService;

	@Autowired
	private MinioService minioService;

	@Operation(summary = "上传文件")
	@PostMapping("/upload")
	public Result<String> upload(@RequestParam("Image") MultipartFile file) {
		// 1. 基础校验
		validateUploadFile(file);
		
		// 2. 根据存储方式处理文件上传
		String storageMethod = sysConfigService.getValue(Constant.STORAGEURL_METHOD);
		String fileUrl = processFileUpload(file, storageMethod);
		
		// 3. 保存文件记录
		saveFileRecord(fileUrl);

        return new Result<String>().ok(fileUrl);
	}

	/**
	 * 校验上传文件
	 */
	private void validateUploadFile(MultipartFile file) {
		if (file.isEmpty()) {
			throw new LinfengException("上传文件不能为空");
		}
		FileCheckUtil.checkSize(maxSize, file.getSize());
		FileCheckUtil.checkFileFormat(file.getOriginalFilename());
	}

	/**
	 * 根据存储方式处理文件上传
	 */
	private String processFileUpload(MultipartFile file, String storageMethod) {
		switch (storageMethod) {
			case "1":
				return handleLocalStorage(file);
			case "2":
				return handleMinioStorage(file);
			default:
				return handleCloudStorage(file);
		}
	}

	/**
	 * 本地存储处理
	 */
	private String handleLocalStorage(MultipartFile file) {
		try {
			// 生成文件信息
			FileInfo fileInfo = generateFileInfo(file);
			
			// 创建目标文件
			File targetFile = createTargetFile(fileInfo);
			
			// 执行文件保存
			file.transferTo(targetFile);
			
			// 生成访问URL
			return buildLocalFileUrl(fileInfo);
			
		} catch (IOException e) {
			throw new LinfengException("本地存储文件上传失败");
		}
	}

	/**
	 * Minio存储处理
	 */
	private String handleMinioStorage(MultipartFile file) {
		try {
			return minioService.uploadFile(file);
		} catch (Exception e) {
			throw new LinfengException("Minio存储文件上传失败");
		}
	}

	/**
	 * 云存储处理
	 */
	private String handleCloudStorage(MultipartFile file) {
		try {
			String suffix = extractFileSuffix(file.getOriginalFilename());
			return OSSFactory.build().uploadSuffix(file.getBytes(), suffix);
		} catch (IOException e) {
			throw new LinfengException("云存储文件上传失败");
		}
	}

	/**
	 * 生成文件信息
	 */
	private FileInfo generateFileInfo(MultipartFile file) {
		String originalFileName = file.getOriginalFilename();
		
		// 检查文件扩展名
		int dotIndex = originalFileName.lastIndexOf(".");
		if (dotIndex == -1) {
			throw new LinfengException("文件格式错误");
		}
		
		// 生成新文件名和相关信息
		String suffix = originalFileName.substring(dotIndex).toLowerCase(Locale.ROOT);
		String newFileName = UUID.randomUUID() + suffix;
		boolean isImage = FileCheckUtil.checkFileIsImage(originalFileName);
		String dateFolder = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).format(new Date());
		
		return new FileInfo(newFileName, dateFolder, isImage);
	}

	/**
	 * 创建目标文件
	 */
	private File createTargetFile(FileInfo fileInfo) {
		String basePath = fileInfo.isImage ? imagePath : videoPath;
		File targetFile = new File(basePath + fileInfo.dateFolder + File.separator + fileInfo.fileName);
		
		// 确保父目录存在
		if (!targetFile.getParentFile().exists() && !targetFile.getParentFile().mkdirs()) {
			throw new LinfengException("文件夹创建失败");
		}
		
		return targetFile;
	}

	/**
	 * 构建本地文件访问URL
	 */
	private String buildLocalFileUrl(FileInfo fileInfo) {
		String baseUrl = sysConfigService.getValue(Constant.LOCAL_STORAGEURL_URL);
		String resourceType = fileInfo.isImage ? "image" : "video";
		return baseUrl + "resource/" + resourceType + "/" + fileInfo.dateFolder + "/" + fileInfo.fileName;
	}

	/**
	 * 提取文件后缀
	 */
	private String extractFileSuffix(String fileName) {
		return fileName.substring(fileName.lastIndexOf("."));
	}

	/**
	 * 保存文件记录
	 */
	private void saveFileRecord(String fileUrl) {
		SysOssEntity ossEntity = new SysOssEntity();
		ossEntity.setUrl(fileUrl);
		ossEntity.setCreateDate(new Date());
		sysOssService.save(ossEntity);
	}

	/**
	 * 文件信息内部类
	 */
	private static class FileInfo {
		final String fileName;
		final String dateFolder;
		final boolean isImage;

		FileInfo(String fileName, String dateFolder, boolean isImage) {
			this.fileName = fileName;
			this.dateFolder = dateFolder;
			this.isImage = isImage;
		}
	}

}
