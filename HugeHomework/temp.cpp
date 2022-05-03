/**
 * @file temp.cpp
 * @author hitwh2200400826 
 * @brief 利用快速傅里叶变换做大整数乘法
 * @version 0.1
 * @date 2022-03-15
 * 
 * @copyright Copyright (c) 2022
 * 
 */
#include <iostream>
#include <stdio.h>
#include <string.h>
#include <fstream>
#include <stdlib.h>
#include <math.h>
#include <vector>
#include <time.h>

#define pi 3.141592653589793238

const long N = 1 << 24;

//s1 s2两个字符数组来存储乘法的两个数
char s1[N];
char s2[N];
//数组来存储答案
int ans[N * 2];

//复数类的基本实现
class Comp
{
private:
    //实部
    double real;

    //虚部
    double imag;
public:
    //无参构造函数
    Comp() {imag = 0; real = 0;}

    //全参构造函数
    Comp(double a, double b) {
        real = a;
        imag = b;
    }

    //getter
    double get_real() const {return real;}

    double get_imag() const {return imag;}

    //运算符重载，根据复数运算规则
    Comp operator+(const Comp& c) {
        return Comp(real + c.get_real(), imag + c.get_imag());
    }

    Comp operator-(const Comp& c) {
        return Comp(real - c.get_real(), imag - c.get_imag());
    }

    Comp operator*(const Comp& c) {
        double ans1 = real * c.get_real() - imag * c.get_imag();
        double ans2 = real * c.get_imag() + imag * c.get_real();
        return Comp(ans1, ans2);
    }

    //这里是复数与实数的除法运算
    Comp operator/(int len) {
        return Comp(real / len, imag / len);
    }

    //赋值操作
    void operator=(const Comp& c ) { 
        real = c.get_real();
        imag = c.get_imag();
    }

    //方便做快速傅里叶逆变换ifft
    void rev() {imag *= -1;}

    //用于初期debug
    void display() {
        printf("%.1lf + %.1lfi\n", real, imag);
    }

};

/**
 * @brief 快速傅里叶变换和逆变换
 * 
 * @param A 指向数组首地址的指针
 * @param n 数组的长度
 * @param flag 0为fft 1为ifft
 * @return Comp* 指向结果数组首地址的指针
 */
Comp* fft(Comp *A, long int n, int flag) {
    //递归终止条件
    if (1 == n) return A;

    //循环控制变量声明
    long int i, j, k;

    //旋转因子和主n次单位根
    Comp w(1.0, 0.0);
    Comp Wn(cos(2 * pi / n), sin(2 * pi / n));
    
    if (flag) Wn.rev();
    //奇数位的偶数位的分开
    Comp *temp_even = (Comp*)malloc(n / 2 * sizeof(Comp));
    Comp *temp_odd = (Comp*)malloc(n / 2 * sizeof(Comp));
    for (i = 0, j = 0, k = 0; i < n; ++i) {
        if (i % 2 == 0) temp_even[j++] = A[i];
        else temp_odd[k++] = A[i];
    }

    //分治分别求奇数位和偶数位快速傅里叶变换结果
    Comp* y0 = fft(temp_even, n / 2, flag);
    Comp* y1 = fft(temp_odd, n / 2, flag);

    Comp temp;

    //合并结果
    for (i = 0; i < n / 2; ++i) {
        Comp t = w * y1[i];
        //根据相消引理和折半引理
        A[i] = y0[i] + t;
        A[i + n / 2] = y0[i] - t;
        w = w * Wn;
    }
    //释放空间
    free(temp_even);
    free(temp_odd);
    return A;
}

/**
 * @brief 通过调用fft实现的大整数乘法
 * 
 */
void MulOfLarNum() {
    //两个数的长度
    long int len1 = strlen(s1);
    long int len2 = strlen(s2);
    long int longer = len1 > len2 ? len1 : len2;
    long int len = 1;
    while (len < longer) len *= 2;
    len *= 2;

    //初始化
    Comp* A = (Comp*)malloc(len * sizeof(Comp));
    Comp* B = (Comp*)malloc(len * sizeof(Comp));
    for (long int i = 0; i < len; ++i) {
        if (i < len1) A[i] = Comp(s1[len1 - i - 1] - '0', 0);
        else A[i] = Comp(0, 0);
        if (i < len2) B[i] = Comp(s2[len2 - i - 1] - '0', 0);
        else B[i] = Comp(0, 0);
    }

    fft(A, len, 0);
    fft(B, len, 0);

    //用AB快速傅里叶变换的结果得到C的点值表达式
    Comp* C = (Comp*)malloc(len * sizeof(Comp));
    for (long int i = 0; i < len; i++) {
        C[i] = A[i] * B[i];
    }

    free(A);
    free(B);
    //快速傅里叶逆变换
    fft(C, len, 1);
    for (long int i = 0; i < len; i++) {
        C[i] = C[i] / len;
        ans[i] = (long int)(C[i].get_real() + 0.5);
    }
    free(C);
    for (long int i = 0; i < len; i++) {
        ans[i + 1] += ans[i] / 10;
        ans[i] %= 10;
    }
}

/**
 * @brief 用于在控制台打印结果
 * 
 */
void ShowAns() {
    long int len1 = strlen(s1);
    long int len2 = strlen(s2);
    //计算结果应该从哪里开始输出，去掉开头的0
    long int len_ans = len1 + len2 + 2;

    while (ans[len_ans] == 0 && len_ans > 0) len_ans--;

    for (long int i = len_ans; i >= 0; i--) {
        printf("%d", ans[i]);
    }   
    printf("\n");
}

void clear_all() {
    memset(s1, '0', sizeof(s1));
    memset(s2, '0', sizeof(s2));
    memset(ans, 0, sizeof(ans));
}

int main() {

    std::ifstream read_file;
    read_file.open("temp.txt");

    std::vector<double> vtime;
    clock_t start, end;

    for (long int j = 0; j < 7; ++j) {
        vtime.push_back(0);
        for (long int i = 0; i < 10; ++i) {
            clear_all();
            read_file >> s1;
            read_file >> s2;
            start = clock();
            MulOfLarNum();
            end = clock();
            vtime[j] = vtime[j] + (end - start);
        }
        vtime[j] = vtime[j] * 1.0 / CLOCKS_PER_SEC / 10 * 1000;
    }
    read_file.close();

    printf("============================Blow is the time=================================\n");
    for (long int i = 0; i < vtime.size(); ++i) {
        printf("%.1lf ", vtime[i]);
    }
    printf("\n");
    return 0;
}