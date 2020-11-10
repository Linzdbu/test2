package com.niudong.demo.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

/**
 * 模拟区块链文件存储方式：guava版
 *
 * @author 牛冬
 */
public class FileStoreUtil {
    // 定义区块链文件大小
    private static final int FILE_SIZE = 1024;// 单位KB

    // 将文件内容写入目标文件:Guava方式
    public static void writeIntoTargetFile(String targetFileName, String content) {
        File newFile = new File(targetFileName);
        try {
            Files.write(content.getBytes(), newFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 将文件内容向后追加写入目标文件:FileWriter方式
    public static void appendToTargetFile(String targetFileName, String content) {
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(targetFileName, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 将文件内容向后追加写入目标文件:Guava方式
    public static void appendToTargetFileByGuava(String targetFileName, String content) {
        File file = new File(targetFileName);
        try {
            Files.append(content, file, Charsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 模拟区块链内容写入文件
    public static void writeIntoFile(String content) {
        try {
            /**
             * 步骤1 查看当前目录下是否有正在写入的loging文件，有则继续使用，无则创建。
             */
            File root = new File(".//");
            // 获取当前文件下的所有文件
            File[] files = root.listFiles();
            if (files == null) {
                // 如果根目录下没有任何文件则创建新的文件，loging后缀表示正在写入的文件
                String targetFileName = ".//blockchain-" + System.currentTimeMillis() + ".loging";
                appendToTargetFileByGuava(targetFileName, content);
                return;
            }

            // 如果根目录下有文件则寻找是否存在loging后缀的文件，loging表示正在写入的文件;有则继续写入，无则创建新的文件
            boolean has = false;
            for (File file : files) {
                String name = file.getName();
                if (name.endsWith(".loging") && name.startsWith("blockchain-")) {
                    // 有则继续写入
                    System.out.println(file.getPath());
                    appendToTargetFileByGuava(file.getPath(), content);
                    has = true;

                    // 写入后如果文件大小超过固定大小则将loging后缀转为log后缀
                    if (file.length() >= FILE_SIZE) {
                        String logPath = file.getPath().replace("loging", "log");
                        File log = new File(logPath);
                        Files.copy(file, log);
                        // 删除已经写满的loging文件
                        file.delete();
                    }
                }
            }

            // 无则创建新的文件
            if (!has) {
                String targetFileName = ".//blockchain-" + System.currentTimeMillis() + ".loging";
                appendToTargetFileByGuava(targetFileName, content);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //模拟区块链内容的写入
    public static void writeIntoBlockFile() {
        List<String> list = new ArrayList<>();
        list.add("AI");
        list.add("BlockChain");
        list.add("BrainScience");

        for (int i = 0; i < 20; i++) {
            list.add(generateVCode(6));
            writeIntoFile(SimpleMerkleTree.getTreeNodeHash(list) + "\n");
        }
    }

    // 根据length长度生成数字串
    public static String generateVCode(int length) {
        Long vCode = new Double((Math.random() + 1) * Math.pow(10, length - 1)).longValue();
        return vCode.toString();
    }

    public static void main(String[] args) {
        String content = "123\n";
        String targetFileName = ".//blockchain-" + System.currentTimeMillis() + ".log";
        //appendToTargetFile(targetFileName, content);
        content = "456\n";
        //appendToTargetFileByGuava(targetFileName, content);
        writeIntoBlockFile();
    }
}
