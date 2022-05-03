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
    int numV;
    List<List<Integer>> paths;//存储最终的路径以及占用的带宽

    //返回cost和flow
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
//                edges.get(i ^ 1).setFlow(edges.get(i ^ 1).flow - minn);
                totalCost += edges.get(i).cost * minn;
            }
            totalFlow += minn;
        }
        return Map.entry(totalCost, totalFlow);
    }

    //构造函数，初始化残差图
    public MinCostMaxFlow(int[][][] net, int numV) {
        edges = new ArrayList<>();
        this.numV = numV;
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

    //加边
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

    //SPFA找路径
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

    //残差图转为矩阵 边长为flow
    private int[][] edge2net() {
        int[][] matrix = new int[this.net.length][this.net.length];
        for (Edge edge : edges) {
            int from = edge.from;
            int to = edge.to;
            int flow = edge.flow;
            matrix[from][to] = flow;
        }
        return matrix;
    }

    //用dfs搜索最终的路径
    private void dfs(int T, int[][]graph, Stack<Integer> stack, boolean[] visited) {
        if (stack.peek() == T) {
            List<Integer> temp = new ArrayList<>();
            List<Integer> path = new LinkedList<>();
            while (!stack.isEmpty()) {
                temp.add(stack.pop());
            }
            stack.push(temp.get(temp.size() - 1));
            int min = Integer.MAX_VALUE;
            for (int i = temp.size() - 2; i >= 1; --i) {
                stack.push(temp.get(i));
//                if (i > 1)
                min = Math.min(min, graph[temp.get(i + 1)][temp.get(i)]);
                min = Math.min(min, graph[temp.get(i)][temp.get(i - 1)]);
                if (i == 1) {
                    path.add(temp.get(i) - this.numV);
//                    System.out.print((temp.get(i) - this.numV)+ " ");
                } else {
                    path.add(temp.get(i));
//                    System.out.print(temp.get(i) + " ");
                }
            }
            stack.push(temp.get(0));
//            System.out.println(min);
            path.add(min);
            paths.add(path);
        } else {
            for (int i = 0; i < graph.length; ++i) {
                if (!visited[i] && graph[stack.peek()][i] > 0) {
                    visited[i] = true;
                    stack.push(i);
                    dfs(T, graph, stack, visited);
                    stack.pop();
                    visited[i] = false;
                }
            }
        }
    }

    //最终答案显示
    public void display(int S, int T) {
        Cost(S, T);
        Stack<Integer> stack = new Stack<>();
        boolean[] visited = new boolean[net.length];
        Arrays.fill(visited, false);
        stack.push(S);
        paths = new ArrayList<>();
        dfs(T, edge2net(), stack, visited);
        System.out.println(paths.size());
        showPaths();
    }

    public void showPaths() {
        for (List<Integer> path : paths) {
            for (Integer integer : path) {
                System.out.print(integer + " ");
            }
            System.out.println();
        }
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
