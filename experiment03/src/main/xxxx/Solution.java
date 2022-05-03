package xxxx;


import java.util.Scanner;

public class Solution {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int numV, numE, people, x, y;
        numV = scanner.nextInt();
        numE = scanner.nextInt();
        people = scanner.nextInt();
        int costPerServer = scanner.nextInt();
        //首先是net这个数组存储的内容是net[i][j]为从i到j总带宽大小和单位网络租用费
        //+2 设置虚拟的开始节点和结束节点，所有的用户节点接到结束节点，所有服务器接到开始节点
        int[][][] net = new int[numV + people + 2][numV + people + 2][2];
        for (int i = 0; i < numE; ++i) {
            x = scanner.nextInt();
            y = scanner.nextInt();
            net[x][y][0] = scanner.nextInt();
            net[y][x][0] = net[x][y][0];
            net[x][y][1] = scanner.nextInt();
            net[y][x][1] = net[x][y][1];
        }
        int point, n, sum = 0;    //sum存储总的需求量
        for (int i = 0; i < people; ++i) {
            point = scanner.nextInt();                   //消费节点id
            n = scanner.nextInt();                       //相连网络节点id
            net[n][point + numV][0] = scanner.nextInt(); //视频带宽需求
            sum += net[n][point + numV][0];
            net[point + numV][people + numV + 1][0] = net[n][point + numV][0];//与结束节点相连接
        }

        Genetic genetic = new Genetic(numV, numE, people, sum, costPerServer, net);
        StringBuilder solution = genetic.getAnswer();
        if (null == solution) {
            System.out.println("无解");
        } else {
            System.out.println();
            genetic.display(solution);
        }
    }
}
