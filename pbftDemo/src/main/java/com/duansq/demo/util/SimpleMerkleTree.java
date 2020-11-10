package com.duansq.demo.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 简化的Merkle树 ROOT hash值计算
 *
 * @author 牛冬
 */
public class SimpleMerkleTree {

    // 按Merkle树思想计算根节点hash值
    public static String getTreeNodeHash(List<String> hashsList) {
        if (hashsList == null || hashsList.size() == 0) {
            return null;
        }

        while (hashsList.size() != 1) {
            hashsList = getMerKleNodeList(hashsList);
        }

        // 最终只剩下根节点
        return hashsList.get(0);
    }

    // 按Merkle树思想计算根节点hash值
    public static List<String> getMerKleNodeList(List<String> contentList) {
        List<String> merKleNodeList = new ArrayList<String>();

        if (contentList == null || contentList.size() == 0) {
            return merKleNodeList;
        }

        int index = 0, length = contentList.size();
        while (index < length) {
            // 获取左孩子节点数据
            String left = contentList.get(index++);

            // 获取右孩子节点数据
            String right = "";
            if (index < length) {
                right = contentList.get(index++);
            }

            // 计算左右孩子节点的父节点的hash值
            String sha2HexValue = SHAUtil.sha256BasedHutool(left + right);
            merKleNodeList.add(sha2HexValue);
        }
        return merKleNodeList;
    }
}
