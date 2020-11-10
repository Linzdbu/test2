package com.duansq.demo.pbftChange;

public class NodeData {

    //节点名称
    private String nodeName;

    //节点类型（0 : 本轮选中的节点；1：好节点  2：新增的节点以及坏节点)
    private int nodeType;

    //节点编号
    private int nodeNumber;

    //密码设定的难度
    private double cryptoDiffcult;

    //分数
    private double score;

    public NodeData() {
    }

    public NodeData(String nodeName, int nodeNumber, double cryptoDiffcult, double score) {
        this.nodeName = nodeName;
        this.nodeNumber = nodeNumber;
        this.cryptoDiffcult = cryptoDiffcult;
        this.score = score;
    }

    //权值，作为选择节点类型的分类
    private double weight;

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public int getNodeType() {
        return nodeType;
    }

    public void setNodeType(int nodeType) {
        this.nodeType = nodeType;
    }

    public int getNodeNumber() {
        return nodeNumber;
    }

    public void setNodeNumber(int nodeNumber) {
        this.nodeNumber = nodeNumber;
    }

    public double getCryptoDiffcult() {
        return cryptoDiffcult;
    }

    public void setCryptoDiffcult(double cryptoDiffcult) {
        this.cryptoDiffcult = cryptoDiffcult;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }


}
