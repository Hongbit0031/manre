package io.linfeng.modules.job.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MySQL数据库备份定时任务
 */
@Component("databaseBackupTask")
public class DatabaseBackupTask implements ITask {
    private Logger logger = LoggerFactory.getLogger(getClass());

    // 保留的备份文件数量
    private static final int KEEP_BACKUP_COUNT = 7;

    @Value("${spring.datasource.druid.username}")
    private String username;

    @Value("${spring.datasource.druid.password}")
    private String password;

    @Value("${spring.datasource.druid.url}")
    private String url;

    /**
     * 清理旧的备份文件，只保留最近的N份
     */
    private void cleanOldBackups(String backupPath, String dbName) {
        File backupDir = new File(backupPath);
        if (!backupDir.exists()) {
            return;
        }

        // 获取所有备份文件
        File[] backupFiles = backupDir.listFiles((dir, name) -> 
            name.startsWith(dbName) && name.endsWith(".sql"));
        
        if (backupFiles == null || backupFiles.length <= KEEP_BACKUP_COUNT) {
            return;
        }

        // 按最后修改时间排序
        Arrays.sort(backupFiles, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));

        // 删除旧文件
        for (int i = KEEP_BACKUP_COUNT; i < backupFiles.length; i++) {
            File oldFile = backupFiles[i];
            if (oldFile.delete()) {
                logger.info("删除旧备份文件：{}", oldFile.getName());
            } else {
                logger.warn("删除旧备份文件失败：{}", oldFile.getName());
            }
        }
    }

    /**
     * 获取MySQL bin目录的路径
     */
    private String getMySqlBinPath() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            // 常见的Windows下MySQL安装路径
            String[] possiblePaths = {
                "D:\\software\\mysql5.7\\MySql\\bin"
            };
            
            for (String path : possiblePaths) {
                File mysqldump = new File(path, "mysqldump.exe");
                if (mysqldump.exists()) {
                    return path;
                }
            }
            throw new RuntimeException("找不到MySQL bin目录，请确保MySQL已正确安装");
        } else {
            // Linux下通常已经在PATH中，直接返回命令名称
            return "";
        }
    }

    /**
     * 从JDBC URL中提取数据库连接信息
     */
    private Map<String, String> extractDatabaseInfo(String jdbcUrl) {
        logger.debug("正在解析数据库URL: {}", jdbcUrl);
        Map<String, String> dbInfo = new HashMap<>();
        
        try {
            // 使用正则表达式匹配主机、端口和数据库名称
            Pattern pattern = Pattern.compile("jdbc:mysql://([^:/]+)(?::(\\d+))?/([^?]+)");
            Matcher matcher = pattern.matcher(jdbcUrl);
            
            if (matcher.find()) {
                String host = matcher.group(1);
                String port = matcher.groupCount() >= 2 && matcher.group(2) != null ? matcher.group(2) : "3306";
                String dbName = matcher.group(3);
                
                dbInfo.put("host", host);
                dbInfo.put("port", port);
                dbInfo.put("dbName", dbName);
                
                logger.debug("成功提取数据库信息: host={}, port={}, dbName={}", host, port, dbName);
                return dbInfo;
            }

            // 如果正则匹配失败，尝试使用简单的字符串处理
            String hostPortPart = jdbcUrl.substring(jdbcUrl.indexOf("//") + 2, jdbcUrl.indexOf("/", jdbcUrl.indexOf("//")+2));
            String[] hostPort = hostPortPart.split(":");
            String host = hostPort[0];
            String port = hostPort.length > 1 ? hostPort[1] : "3306";
            
            int lastSlash = jdbcUrl.lastIndexOf("/");
            int questionMark = jdbcUrl.indexOf("?", lastSlash);
            String dbName = questionMark > lastSlash ? 
                jdbcUrl.substring(lastSlash + 1, questionMark) : 
                jdbcUrl.substring(lastSlash + 1);
            
            dbInfo.put("host", host);
            dbInfo.put("port", port);
            dbInfo.put("dbName", dbName);
            
            logger.debug("使用备选方法提取数据库信息: host={}, port={}, dbName={}", host, port, dbName);
            return dbInfo;
        } catch (Exception e) {
            logger.error("解析数据库URL时发生错误: {}", jdbcUrl, e);
            throw new RuntimeException("解析数据库URL失败: " + e.getMessage(), e);
        }
    }

    /**
     * 判断是否为本地数据库连接
     */
    private boolean isLocalDatabase(String host) {
        return "localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host);
    }

    @Override
    public void run(String params) {
        Process process = null;
        FileOutputStream outputStream = null;
        try {
            // 从URL中提取数据库连接信息
            Map<String, String> dbInfo = extractDatabaseInfo(url);
            String host = dbInfo.get("host");
            String port = dbInfo.get("port");
            String dbName = dbInfo.get("dbName");
            
            logger.info("准备备份数据库: {}，主机: {}，端口: {}", dbName, host, port);
            
            // 创建备份文件夹
            String backupPath = "backup" + File.separator + "mysql" + File.separator;
            File backupDir = new File(backupPath);
            if (!backupDir.exists()) {
                backupDir.mkdirs();
            }

            // 生成备份文件名（使用时间戳）
            String fileName = String.format("%s_%s.sql", dbName, 
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
            String filePath = backupPath + fileName;

            // 获取MySQL bin目录
            String mysqlBinPath = getMySqlBinPath();
            String mysqldumpCmd = mysqlBinPath.isEmpty() ? "mysqldump" : 
                                mysqlBinPath + File.separator + "mysqldump";

            // 构建命令
            List<String> commandList = new ArrayList<>();
            commandList.add(mysqldumpCmd);
            commandList.add("-u" + username);
            commandList.add("-p" + password);
            
            // 添加主机和端口参数（对于远程数据库）
            commandList.add("-h" + host);
            commandList.add("-P" + port);
            
            commandList.add("--databases");
            commandList.add(dbName);
            
            // 添加其他有用的参数
            commandList.add("--single-transaction");  // 避免锁表
            commandList.add("--set-gtid-purged=OFF"); // 避免GTID相关警告
            
            String[] command = commandList.toArray(new String[0]);

            // 执行命令
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true); // 合并错误和标准输出流
            process = processBuilder.start();

            // 创建输出文件
            outputStream = new FileOutputStream(filePath);
            InputStream inputStream = process.getInputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // 等待进程完成
            int processComplete = process.waitFor();

            if (processComplete == 0) {
                logger.info("数据库备份成功，备份文件：{}", filePath);
                // 清理旧的备份文件
                cleanOldBackups(backupPath, dbName);
            } else {
                // 读取错误信息
                byte[] errorBuffer = new byte[1024];
                int errorBytesRead;
                StringBuilder errorMsg = new StringBuilder();
                while ((errorBytesRead = process.getErrorStream().read(errorBuffer)) != -1) {
                    errorMsg.append(new String(errorBuffer, 0, errorBytesRead));
                }
                String error = errorMsg.toString();
                logger.error("数据库备份失败，错误信息：{}", error);
                throw new RuntimeException("数据库备份失败: " + error);
            }

        } catch (Exception e) {
            logger.error("数据库备份异常", e);
            throw new RuntimeException("数据库备份异常: " + e.getMessage(), e);
        } finally {
            // 关闭资源
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.error("关闭输出流失败", e);
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
    }
} 