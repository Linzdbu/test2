package com.duansq.demo.pbft;

import com.duansq.demo.util.TimerManager;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.internal.thread.ICountDown;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.duansq.demo.pbft.Pbft.transCount;
import static com.duansq.demo.pbft.convertDate.calculateTime;

public class PbftMain {

    static Logger logger = LoggerFactory.getLogger(PbftMain.class);

    public static final int size = 10;

    private static List<Pbft> nodes = Lists.newArrayList();

    private static Random r = new Random();

    private static long[] net = new long[50000];


    public static void main(String[] args) throws InterruptedException {

        //节点选取的方法
        //初始化节点数据
        System.out.println("初始化数据");
        //4个点、8个点、12个点、20、30到90个点
        //每个节点数目，重复5次实验，取平均数



		/*List<NodeData> dataList = new ArrayList<>();
		dataList = initNodeData.getinitList();*/
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        String str1 = sdf.format(new Date());
        System.out.println("共识开始时间为" + str1);

        //将所有节点添加到List里面
        for (int i = 0; i < size; i++) {
            nodes.add(new Pbft(i, 10).start());
        }

        //设定所有网络的延时，模拟恶意节点
        // 初始化模拟网络
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i != j) {
                    // 随机延时
                    net[i * 10 + j] = RandomUtils.nextLong(10, 20);
                } else {
                    net[i * 10 + j] = 10;
                }
            }
        }

        // 模拟请求端发送请求
        //模拟发送3条数据
        for (int i = 0; i < 3; i++) {
            //随机选取主节点，可以修改成按照一定标准队列里，随机选取的节点
            int node = r.nextInt(size);
            nodes.get(node).req("第" + (i + 1) + "条数据，由主节点" + node + "发出");
            Thread.sleep(1000);
        }

        // 1秒后，主节点宕机

        //nodes.get(2).req("第二条数据,由主节点2发出");
        //nodes.get(3).req("第三条数据,由主节点3发出");
		/*for(int i=2;i<3;i++){
			nodes.get(i).req("第二条数据,由主节点"+i+"发出");
		}

		for(int i=1;i<2;i++){
			nodes.get(i).req("第三条数据,由主节点"+i+"发出");
		}*/

	/*	nodes.get(0).close();


		
		Thread.sleep(1000);
		// 1秒后，恢复
		nodes.get(0).back();
		*/


        String str2 = sdf.format(new Date());
        System.out.println("共识结束时间为" + str2);
        System.out.println("达成共识的时间为" + calculateTime(str1, str2));
        System.out.println();


    }

    /**
     * 广播消息
     *
     * @param msg
     */
    public static void publish(PbftMsg msg) {
        logger.info("广播消息[" + msg.getNode() + "]: 消息内容为" + msg);
        for (Pbft pbft : nodes) {
            // 模拟网络时延
            TimerManager.schedule(() -> {
                pbft.push(new PbftMsg(msg));
                transCount.getAndIncrement();
                return null;
            }, net[msg.getNode() * 10 + pbft.getIndex()]);
        }
    }

    /**
     * 发送消息到指定节点
     *
     * @param toIndex
     * @param msg
     */
    public static void send(int toIndex, PbftMsg msg) {
        // 模拟网络时延
        TimerManager.schedule(() -> {
            nodes.get(toIndex).push(msg);
            return null;
        }, net[msg.getNode() * 10 + toIndex]);
    }


}
