package xu.sol;


import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Solution {

    //为了测试多路径输出，本图与示例略有不同
    //意义：graph[i][j][0]为第i行j列向下的路长,[i][j][1]为向右的路长，不存在的设置为-1
    private static int [][][] graph = {
            {{1, 3}, {0, 2}, {2, 4}, {4, 0}, {3, -1}},
            {{4, 3}, {6, 2}, {5, 4}, {2, 2}, {1, -1}},
            {{4, 0}, {4, 7}, {5, 3}, {2, 4}, {1, -1}},
            {{5, 3}, {6, 3}, {8, 0}, {5, 2}, {3, -1}},
            {{-1, 1}, {-1, 10}, {-1, 2}, {-1, 2}, {-1, -1}}
    };

    public static void Manhattan() {
        int[][] dp = new int[5][5];
        int len = graph[0][0][1];
        for (int i = 1; i < graph[0].length; ++i) {
            dp[0][i] = len;
            len += graph[0][i][1];
        }
        len = graph[0][0][0];
        for (int i = 1; i < graph.length; ++i) {
            dp[i][0] = len;
            len += graph[i][0][0];
        }
        for (int i = 1; i < dp.length; ++i) {
            for (int j = 1; j < dp[i].length; ++j) {
                dp[i][j] = Math.max(dp[i - 1][j] + graph[i - 1][j][0],
                        dp[i][j - 1] + graph[i][j - 1][1]);
            }
        }
        showShowWay(dp);
        backtracking(dp, 4, 4, new LinkedList<String>());
    }

    //回溯找路径，可以把全部路径输出
    public static void backtracking(int[][] dp, int i, int j, List<String> path) {
        if (i == 0 && j == 0) {
            for (int k = path.size() - 1; k >= 0; --k) {
                System.out.print(path.get(k));
            }
            System.out.println();
        }
        else if (i < 0 || j < 0) {
            return;
        }
        if (i > 0 && dp[i - 1][j] + graph[i - 1][j][0] == dp[i][j]) {
            path.add("↓");
            backtracking(dp, i - 1, j, path);
            path.remove(path.size() - 1);
        }
        if (j > 0 && dp[i][j - 1] + graph[i][j - 1][1] == dp[i][j]) {
            path.add("→");
            backtracking(dp, i, j - 1, path);
            path.remove(path.size() - 1);
        }

    }

    public static void showWay(int[][] dp, int i, int j) {
        Stack<String> path = new Stack<String>();
        while (i > 0 || j > 0) {
            if (i > 0 && dp[i][j] == dp[i - 1][j] + graph[i - 1][j][0]) {
                path.push("↓");
                i--;
            }
            else if (j > 0 && dp[i][j] == dp[i][j - 1] + graph[i][j - 1][1]) {
                path.push("→");
                j--;
            }
        }
        while (!path.isEmpty()) {
            System.out.print(path.pop() + " ");
        }
        System.out.println();
    }

    public static void showShowWay(int[][] dp) {
        for (int i = 0; i < dp.length; ++i) {
            for (int j = 0; j < dp[i].length; ++j) {
                System.out.printf("%3d ", dp[i][j]);
            }
            System.out.println();
        }
    }
    /*
    public static int longestPalindromeSubseq(String s) {
        int[][] dp = new int[s.length()][s.length()];
//        dp[i][j] i 到 j 最长的回文子序列的长度
//        if s[i] == s[j]:
//              dp[i][j] = dp[i + 1][j - 1] + 2
//        else:
//              dp[i][j] = dp[i][j - 1]
        for (int i = 0; i < s.length(); ++i) dp[i][i] = 1;
        for (int j = 1; j < s.length(); ++j) {
            for (int i = j - 1; i >= 0; --i) {
                if (s.charAt(i) == s.charAt(j)) {
                    if (j - i == 1) dp[i][j] = 2;
                    else dp[i][j] = dp[i + 1][j - 1] + 2;
                }
                else dp[i][j] = Math.max(dp[i][j - 1], dp[i + 1][j]);
            }
        }
        int ans = 0;
        for (int i = 0; i < s.length(); ++i) {
            for (int j = 0; j < s.length(); ++j) {
                ans = Math.max(ans, dp[i][j]);
            }
        }
        return ans;
    }*/


    public static void main(String[] args) {
        Manhattan();
    }
}
