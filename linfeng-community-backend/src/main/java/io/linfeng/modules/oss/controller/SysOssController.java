package io.linfeng.modules.oss.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import io.linfeng.common.exception.LinfengException;
import io.linfeng.common.utils.*;
import io.linfeng.common.validator.ValidatorUtils;
import io.linfeng.common.validator.group.AliyunGroup;
import io.linfeng.common.validator.group.QiniuGroup;
import io.linfeng.modules.oss.factory.CloudStorageConfig;
import io.linfeng.modules.oss.factory.LocalStorageConfig;
import io.linfeng.modules.oss.factory.OSSFactory;
import io.linfeng.modules.oss.entity.SysOssEntity;
import io.linfeng.modules.oss.service.SysOssService;
import io.linfeng.modules.oss.service.impl.MinioService;
import io.linfeng.modules.sys.service.SysConfigService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 文件上传
 *
 */
@RestController
@RequestMapping("sys/oss")
@Tag(name = "管理端——文件上传")
public class SysOssController {

	@Value("${cloudstorage.admin-max-size}")
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

    private final static String KEY = ConfigConstant.CLOUD_STORAGE_CONFIG_KEY;
	
	/**
	 * 列表
	 */
	@Operation(summary = "文件列表")
	@GetMapping("/list")
	@RequiresPermissions("sys:oss:all")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = sysOssService.queryPage(params);

		return R.ok().put("page", page);
	}


    /**
     * 云存储配置信息
     */
	@Operation(summary = "云存储配置信息")
    @GetMapping("/config")
    @RequiresPermissions("sys:oss:all")
    public R config(){
        CloudStorageConfig config = sysConfigService.getConfigObject(KEY, CloudStorageConfig.class);
		String storageMethod = sysConfigService.getValue(Constant.STORAGEURL_METHOD);
		String localStorageUrl = sysConfigService.getValue(Constant.LOCAL_STORAGEURL_URL);
        return R.ok()
				.put("config", config)
				.put("storageMethod",storageMethod)
				.put("localStorageUrl",localStorageUrl);
    }


	/**
	 * 保存云存储配置信息
	 */
	@Operation(summary = "保存云存储配置信息")
	@PostMapping("/saveConfig")
	@RequiresPermissions("sys:oss:all")
	public R saveConfig(@RequestBody CloudStorageConfig config){
		//校验类型
		ValidatorUtils.validateEntity(config);

		if(config.getType() == Constant.CloudService.QINIU.getValue()){
			//校验七牛数据
			ValidatorUtils.validateEntity(config, QiniuGroup.class);
		}else if(config.getType() == Constant.CloudService.ALIYUN.getValue()){
			//校验阿里云数据
			ValidatorUtils.validateEntity(config, AliyunGroup.class);
		}

        sysConfigService.updateValueByKey(KEY, new Gson().toJson(config));
		sysConfigService.updateValueByKey(Constant.STORAGEURL_METHOD, "0");
		return R.ok();
	}


	@Operation(summary = "保存本地存储配置信息")
	@PostMapping("/saveLocalConfig")
	@RequiresPermissions("sys:oss:all")
	public R saveLocalConfig(@RequestBody LocalStorageConfig config){
		if(config.getLocal().equals("1")){
			sysConfigService.updateValueByKey(Constant.STORAGEURL_METHOD, config.getLocal());
			sysConfigService.updateValueByKey(Constant.LOCAL_STORAGEURL_URL, config.getLocalUrl());
		}
		if(config.getLocal().equals("2")){
			sysConfigService.updateValueByKey(Constant.STORAGEURL_METHOD, config.getLocal());
		}
		return R.ok();
	}


	/**
	 * 上传文件
	 */
	@Operation(summary = "上传文件")
	@PostMapping("/upload")
	@RequiresPermissions("sys:oss:all")
	public R upload(@RequestParam("file") MultipartFile file) {
		if (file.isEmpty()) {
			throw new LinfengException("上传文件不能为空");
		}
		FileCheckUtil.checkSize(maxSize, file.getSize());
		FileCheckUtil.checkFileFormat(file.getOriginalFilename());
		String storageMethod = sysConfigService.getValue(Constant.STORAGEURL_METHOD);
		if(storageMethod.equals("1")){
			//如果本地存储开启，则走本地存储
			String fileName = file.getOriginalFilename();
			int index = fileName.lastIndexOf(".");
			if (index == -1) {
				throw new LinfengException("文件格式错误");
			}
			boolean isImg = FileCheckUtil.checkFileIsImage(fileName);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
			String dateFolder = dateFormat.format(new Date());
			String suffixName = fileName.substring(index).toLowerCase(Locale.ROOT);
			fileName = UUID.randomUUID() + suffixName;
			File files;
			if(isImg){
				files = new File(imagePath + dateFolder+ File.separator + fileName);
			}else{
				files = new File(videoPath + dateFolder+ File.separator + fileName);
			}

			if (!files.getParentFile().exists()) {
				if (!files.getParentFile().mkdirs()) {
					throw new LinfengException("文件夹创建失败");
				}
			}
			try {
				file.transferTo(files);
			} catch (IOException e) {
				e.printStackTrace();
				return R.error("文件上传失败");
			}
			String res;
			String url = sysConfigService.getValue(Constant.LOCAL_STORAGEURL_URL);
			if(isImg){
				res=  url + "resource/image/" + dateFolder + "/" + fileName;
			}else{
				res=  url + "resource/video/" + dateFolder + "/" + fileName;
			}
			SysOssEntity ossEntity = new SysOssEntity();
			ossEntity.setUrl(res);
			ossEntity.setCreateDate(new Date());
			sysOssService.save(ossEntity);
			return R.ok().put("url",res);
		}else if(storageMethod.equals("2")){
			//minio存储
			try {
				String fileUrl = minioService.uploadFile(file);
				SysOssEntity ossEntity = new SysOssEntity();
				ossEntity.setUrl(fileUrl);
				ossEntity.setCreateDate(new Date());
				sysOssService.save(ossEntity);
				return R.ok().put("url",fileUrl);
			} catch (Exception e) {
				return R.error("文件上传失败");
			}
		}
		//如果未开启本地存储则选择 云存储 上传文件
		String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		String url;
		try {
			url = OSSFactory.build().uploadSuffix(file.getBytes(), suffix);
		} catch (IOException e) {
			e.printStackTrace();
			return R.error("上传失败");
		}

		//保存文件信息
		SysOssEntity ossEntity = new SysOssEntity();
		ossEntity.setUrl(url);
		ossEntity.setCreateDate(new Date());
		sysOssService.save(ossEntity);

		return R.ok().put("url",url);
	}

	/**
	 * 上传文件至富文本编辑器
	 */
	@Operation(summary = "上传文件至富文本编辑器")
	@PostMapping("/uploadToEditor")
	@RequiresPermissions("sys:oss:all")
	public JSONObject uploadToEditor(@RequestParam("file") MultipartFile file) throws Exception {
		if (file.isEmpty()) {
			throw new LinfengException("上传文件不能为空");
		}
		FileCheckUtil.checkFileFormat(file.getOriginalFilename());
		//如果本地存储开启，则走本地存储
		String storageMethod = sysConfigService.getValue(Constant.STORAGEURL_METHOD);
		if(storageMethod.equals("1")){
			String fileName = file.getOriginalFilename();
			int index = fileName.lastIndexOf(".");
			if (index == -1) {
				throw new LinfengException("文件格式错误");
			}
			boolean isImg = FileCheckUtil.checkFileIsImage(fileName);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
			String dateFolder = dateFormat.format(new Date());
			String suffixName = fileName.substring(index).toLowerCase(Locale.ROOT);
			fileName = UUID.randomUUID() + suffixName;
			File files;
			if(isImg){
				files = new File(imagePath + dateFolder+ File.separator + fileName);
			}else{
				files = new File(videoPath + dateFolder+ File.separator + fileName);
			}

			if (!files.getParentFile().exists()) {
				if (!files.getParentFile().mkdirs()) {
					throw new LinfengException("文件夹创建失败");
				}
			}
			try {
				file.transferTo(files);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			String res;
			String url = sysConfigService.getValue(Constant.LOCAL_STORAGEURL_URL);
			if(isImg){
				res=  url + "resource/image/" + dateFolder + "/" + fileName;
			}else{
				res=  url + "resource/video/" + dateFolder + "/" + fileName;
			}
			SysOssEntity ossEntity = new SysOssEntity();
			ossEntity.setUrl(res);
			ossEntity.setCreateDate(new Date());
			sysOssService.save(ossEntity);
			//响应对象要求符合富文本要求
			JSONObject result = new JSONObject();
			JSONObject data = new JSONObject();
			data.put("url", res);
			data.put("alt", ""); // 如果不需要 alt 字段，可以省略这行
			data.put("href", ""); // 如果不需要 href 字段，可以省略这行
			result.put("errno", 0);
			result.put("data", data);
			return result;
		}else if(storageMethod.equals("2")){
			//minio存储
			try {
				String fileUrl = minioService.uploadFile(file);
				SysOssEntity ossEntity = new SysOssEntity();
				ossEntity.setUrl(fileUrl);
				ossEntity.setCreateDate(new Date());
				sysOssService.save(ossEntity);
				//响应对象要求符合富文本要求
				JSONObject result = new JSONObject();
				JSONObject data = new JSONObject();
				data.put("url", fileUrl);
				data.put("alt", ""); // 如果不需要 alt 字段，可以省略这行
				data.put("href", ""); // 如果不需要 href 字段，可以省略这行
				result.put("errno", 0);
				result.put("data", data);
				return result;
			} catch (Exception e) {
				throw new LinfengException("minio存储文件上传失败");
			}
		}
		//如果未开启本地存储则选择 云存储 上传文件
		String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		String url;
		try {
			url = OSSFactory.build().uploadSuffix(file.getBytes(), suffix);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		//保存文件信息
		SysOssEntity ossEntity = new SysOssEntity();
		ossEntity.setUrl(url);
		ossEntity.setCreateDate(new Date());
		sysOssService.save(ossEntity);
		//响应对象要求符合富文本要求
		JSONObject result = new JSONObject();
		JSONObject data = new JSONObject();
		data.put("url", url);
		data.put("alt", ""); // 如果不需要 alt 字段，可以省略这行
		data.put("href", ""); // 如果不需要 href 字段，可以省略这行
		result.put("errno", 0);
		result.put("data", data);

		return result;
	}


	/**
	 * 删除
	 */
	@Operation(summary = "删除")
	@PostMapping("/delete")
	@RequiresPermissions("sys:oss:all")
	public R delete(@RequestBody Long[] ids){
		sysOssService.removeByIds(Arrays.asList(ids));

		return R.ok();
	}

}
