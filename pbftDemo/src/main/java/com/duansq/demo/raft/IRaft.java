package com.duansq.demo.raft;

public interface IRaft {

    void close();

    void back();

    int getIndex();

    void push(RaftMsg raftMsg);


}
