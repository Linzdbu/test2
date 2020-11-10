package com.duansq.demo.pbft;

import lombok.Data;

@Data
public class NodeData {

    //序号
    private int index;

    //节点名称
    private String nodeName;

    //节点类型（0 : 本轮选中的节点；1：好节点  2：新增的节点以及坏节点)
    private int nodeType;

    //节点编号
    private int nodeNumber;

    //签名算法
    private String digestName;

    //密码设定的冗余值
    private double cryptoDiffcult;

    //加入证书
    private String addCert;

    //交易证书
    private String roundCert;

    //证书冗余值
    private Double certDiffcult;

    //物理地址
    private String address;

    //IP地址
    private String ipAddress;

    //公钥
    private String publicKey;

    //周期数
    private int timeRound;

    //节点权值
    private double score;

    public NodeData() {
    }


}
