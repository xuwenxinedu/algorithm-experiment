package xxxx;


import java.util.*;

public class MinCostMaxFlow {
    ArrayList<Edge> edges;
    int tot;
    int[][][] net;
    int[] head;
    boolean[] vis;
    int[] pre;
    int[] dis;

    public Map.Entry<Integer, Integer> Cost(int S, int T) {
        int totalCost = 0;
        int totalFlow = 0;
        while (SPFA(S, T)) {
            int minn = Integer.MAX_VALUE;
            for (int i = pre[T]; i != -1; i = pre[edges.get(i ^ 1).to]) { // i ^ 1取出来的是对边 x,y -> y,x
                minn = Math.min(minn, edges.get(i).cap - edges.get(i).flow);
            }
            for (int i = pre[T]; i != -1; i = pre[edges.get(i ^ 1).to]) {
                edges.get(i).setFlow(edges.get(i).flow + minn);
                edges.get(i ^ 1).setFlow(edges.get(i ^ 1).flow - minn);
                totalCost += edges.get(i).cost * minn;
            }
            totalFlow += minn;
        }
        return Map.entry(totalCost, totalFlow);
    }

    public MinCostMaxFlow(int[][][] net) {
        edges = new ArrayList<>();
        this.net = net;
        tot = 0;
        int len = net.length;
        head = new int[len];
        dis = new int[len];
        vis = new boolean[len];
        pre = new int[len];
        Arrays.fill(head, -1);
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                if (net[i][j][0] > 0) {
                    addEdge(i, j);
                }
            }
        }
    }

    private void addEdge(int x, int y) {
        if (net[x][y][0] == Integer.MAX_VALUE) {//虚拟源点到服务器是单向的，用户到虚拟汇点也是单向的但能放else里处理
            edges.add(new Edge(y, x, Integer.MAX_VALUE, 0, 0, head[y]));
            head[y] = tot++;
            edges.add(new Edge(x, y, 0, 0, 0, head[x]));
            head[x] = tot++;
        } else {
            edges.add(new Edge(x, y, net[x][y][0], 0, net[x][y][1], head[x]));
            head[x] = tot++;
            edges.add(new Edge(y, x, net[y][x][0], 0, net[y][x][1], head[y]));
            head[y] = tot++;
        }
    }

    private boolean SPFA(int S, int T) {
        Queue<Integer> queue = new LinkedList<>();
        Arrays.fill(dis, Integer.MAX_VALUE);
        Arrays.fill(vis, false);
        Arrays.fill(pre, -1);

        dis[S] = 0;
        queue.add(S);
        vis[S] = true;
        while (!queue.isEmpty()) {
            int x = queue.poll();
            vis[x] = false;
            for (int i = head[x]; i != -1; i = edges.get(i).next) {
                int y = edges.get(i).to;
                int cost = edges.get(i).cost;
                int cap = edges.get(i).cap;
                int flow = edges.get(i).flow;
                if (dis[y] > dis[x] + cost && cap > flow) {
                    dis[y] = dis[x] + cost;
                    pre[y] = i;
                    if (!vis[y]) {
                        vis[y] = true;
                        queue.add(y);
                    }
                }
            }
        }
        return pre[T] != -1;

    }


}

class Edge {
    int to;
    int from;
    int cap;
    int flow;
    int cost;
    int next;

    public void setFlow(int f) {
        this.flow = f;
    }

    public Edge() {
    }

    public Edge(int from, int to, int cap, int flow, int cost, int next) {
        this.to = to;
        this.from = from;
        this.cap = cap;
        this.flow = flow;
        this.cost = cost;
        this.next = next;
    }
}
