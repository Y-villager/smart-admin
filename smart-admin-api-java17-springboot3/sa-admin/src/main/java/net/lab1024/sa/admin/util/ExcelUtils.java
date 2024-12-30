package net.lab1024.sa.admin.util;

import com.alibaba.excel.EasyExcel;
import net.lab1024.sa.base.common.util.SmartRequestUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class ExcelUtils {

    public static String saveFailedDataToExcel(List<?> failedDataList, Class<?> tClass, String path, String failedDataName) {
        Long userId = SmartRequestUtil.getRequestUserId();
        // 构建文件保存路径
        String userFolder = path + userId + "\\";
        File directory = new File(userFolder);

        // 如果文件夹不存在，创建文件夹
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 构建文件路径
        File file = new File(userFolder + failedDataName);

        // 使用 EasyExcel 保存失败的数据到 Excel 文件
        try (OutputStream os = new FileOutputStream(file)) {
            // 动态传递类型到 EasyExcel
            EasyExcel.write(os, tClass)
                    .sheet("失败记录")
                    .doWrite(failedDataList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        return null;

    }
}
