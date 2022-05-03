package xxxx;

import java.util.*;

public class Genetic {
    private final double P_OF_CROSS = 0.25; // 0.25概率发生单点交叉
    private final double P_OF_MUTATION = 0.05; // 0.05概率发生变异
    private final int FIT = 100000;         //适应度函数的反比例系数
    private final int GROUP_SIZE = 50;      //种群大小
    private final int GENERATION_SIZE = 100;//迭代次数
    private int numV;                       //节点个数
    private int numE;                       //边的个数
    private int people;                     //消费节点个数
    private int maxFlow;                    //消费节点所需要的流量和
    private int costPerServer;              //每台服务器耗费
    private int[][][] net;                  //读入的网络

    public StringBuilder getAnswer() {
        List<StringBuilder> group = new LinkedList<>();
        for (int i = 0; i < GROUP_SIZE; ++i) {
            group.add(randCode());
        }
        ArrayList<StringBuilder> best = new ArrayList<>();
        for (int i = 0; i < GENERATION_SIZE; i++) {
            group = select(group, best);
            crossover(group);
            mutation(group);
        }
//        System.out.println(best.get(best.size() - 1));
        double f1 = getFitness(best.get(best.size() - 1));
        double f2 = getFitness(best.get(best.size() - 2));
        if (0 != f1 || 0 != f2) {
//            System.out.println(FIT * 1.0 / f);
            return f1 > f2 ? best.get(best.size() - 1) : best.get(best.size() - 2);
        } else {
            return null;
        }
    }

    //变异
    public void mutation(List<StringBuilder> list) {
        for (int i = 0; i < list.size(); ++i) {
            if (Math.random() < P_OF_MUTATION) {
                int index = new Random().nextInt(numV);
                char c = list.get(i).charAt(index);
                StringBuilder temp = list.get(i);
                if (c == '0') {
                    temp.setCharAt(index, '1');
                } else {
                    temp.setCharAt(index, '0');
                }
                list.set(i, temp);
            }
        }
    }

    //单点交叉
    private void crossover(List<StringBuilder> list) {
        Random random = new Random();
        int n = random.nextInt(numV - 1) + 1;
        List<StringBuilder> set = new ArrayList<>();
        //[0, n] [n + 1, numV - 1]
        list.removeIf(builder -> {
            if (Math.random() < P_OF_CROSS) {
                set.add(builder);
                return true;
            }
            return false;
        });
        if (set.size() >= 1) {
            for (int i = 1; i < set.size(); ++i) {
                list.add(new StringBuilder(set.get(i - 1).substring(0, n)).append(set.get(i).substring(n)));
            }
            list.add(randCode());
        }
    }

    //适应度计算 简单的反比例函数
    private double getFitness(StringBuilder stringBuilder) {
        List<Integer> list = translate(stringBuilder);
        if (0 == list.size()) {
            return 0;
        }
        mark(list);
        double sum = 0;
        sum += costPerServer * list.size();
        MinCostMaxFlow flow = new MinCostMaxFlow(net, numV);
        Map.Entry<Integer, Integer> entry = flow.Cost(numV + people, numV + people + 1);//k,v -> cost, flow
        sum += entry.getKey();

        clear(list);
        if (entry.getValue() < maxFlow) {   //不能满足的直接选择淘汰
            return 0;
        }
        return 1.0 * FIT / sum;
    }

    //选择：轮盘对赌
    private List<StringBuilder> select(List<StringBuilder> list, ArrayList<StringBuilder> best) {
        List<StringBuilder> set = new LinkedList<>();
        List<Double> fitness = new ArrayList<>();
        double temp;
        double sum = 0;
        for (StringBuilder builder : list) {
            temp = getFitness(builder);
            sum += temp;
            fitness.add(temp);
        }
        double max = 0;
        int index = 0;
        for (int i = 0; i < fitness.size(); i++) {
            if (fitness.get(i) > max) {
                index = i;
                max = fitness.get(i);
            }
            fitness.set(i, fitness.get(i) / sum);
        }


//        System.out.println("本次最好的基因型是：" + list.get(index));
//        double f = getFitness(list.get(index));
//        if (f != 0) {
//            System.out.println("所花费的费用为" + FIT * 1.0 / getFitness(list.get(index)));
//        } else {
//            System.out.println("不能满足所有用户需求");
//        }
//        System.out.println();


        best.add(list.get(index));
        for (int j = 0; j < fitness.size(); j++) {
            sum = 0;
            double slice = Math.random();
            for (int i = 0; i < fitness.size(); ++i) {
                sum += fitness.get(i);
                if (sum > slice) {
                    set.add(list.get(i));
                    break;
                }
            }
        }
        return set;
    }

    //随机产生基因
    private StringBuilder randCode() {
        StringBuilder s = new StringBuilder();
        char temp;
        Random r = new Random();
        for (int i = 0; i < numV; ++i) {
            temp = (char)(r.nextInt(2) + 48);
            s.append(temp);
        }
        return s;
    }

    //翻译基因型
    private List<Integer> translate(StringBuilder s) {
        List<Integer> ans = new LinkedList<>();
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == '1') {
                ans.add(i);
            }
        }
        return ans;
    }

    //在网络中取消设置
    private void clear(List<Integer> nums) {
        for (int num : nums) {
            net[num][numV + people][0] = 0;
        }
    }

    //在网络中设置服务器到源点（与事实相反，方便之后在残差图中加边）
    private void mark(List<Integer> nums) {
        for (int num : nums) {
            net[num][numV + people][0] = Integer.MAX_VALUE;
        }
    }

    //展示结果
    public void display(StringBuilder builder) {
        List<Integer> nums = translate(builder);
        mark(nums);

        new MinCostMaxFlow(net, numV).display(numV + people, numV + people + 1);

        clear(nums);
    }

    public Genetic() {
    }

    public Genetic(int numV, int numE, int people, int maxFlow, int costPerServer ,int[][][] net) {
        this.numV = numV;
        this.numE = numE;
        this.people = people;
        this.maxFlow = maxFlow;
        this.costPerServer = costPerServer;
        this.net = net;
    }

}
