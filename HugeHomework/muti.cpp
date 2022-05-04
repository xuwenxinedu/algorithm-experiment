#include <iostream>
#include <sstream>
#include <cstring>
#include <fstream>
#include <sys/time.h>
#include <vector>
using namespace std;
/**
 * @brief 需要改文件路径和测试时间的语句
 * 
 */
 
//函数声明//
//大整数减法;
string subBI(string A, string B);
//数字转字符串
string Int_to_String(int num);
//字符串转数字
int String_to_Int(string s);
//去掉前面的0
string remove_zero(string s);
//大整数加法
string plusBI(string A, string B);
//判断结果正负号
string judge_outcome(string A, string B);
//补齐符号
string complete_sign(string A);
//
 
//大整数加法
string plusBI(string A, string B)
{
    //获取字符串A、B中最长的那个字符串的长度（A、B是只是数字部分的字符串）
    int length = A.length() >= B.length() ? A.length() : B.length();
 
    //temp：中间运算的值，result：最后的结果
    string temp = "", result = "";
 
    //对齐A、B
    string part_A(length - A.length(), '0');
    A = part_A + A;
    string part_B(length - B.length(), '0');
    B = part_B + B;
 
    //value_A：读取的A上的每一位值；value_B：读取的B上的每一位值；count：计算是否需要借位
    int value_A, value_B, count = 0;
 
    //从低位开始一位一位的累减
    for (int i = 1; i <= length; i++)
    {
        //获取四个整数当前位的值
        value_A = String_to_Int(A.substr(length - i, 1));
        value_B = String_to_Int(B.substr(length - i, 1));
        temp = Int_to_String(value_A + value_B);  //以字符串形式存储当前位相加的结果
 
        if (i == 1)  //最低位相加无须考虑进位
        {
            result = temp;
        }
        else if (result.length() > i - 1)  //非最低位相加需考虑已加结果是否有进位
        {
            //有进位的情况
            value_A = String_to_Int(result.substr(0, 1));
            value_B = String_to_Int(temp);
            temp = Int_to_String(value_A + value_B);
            result = temp + result.substr(1);
        }
        else  //无进位的情况
        {
            result = temp + result;
        }
    }
    return result;
}
 
//大整数减法;
string subBI(string A, string B)
{
    //获取字符串A、B中最长的那个字符串的长度（A、B是只是数字部分的字符串）
    int length = A.length() >= B.length() ? A.length() : B.length();
 
    //temp：中间运算的值，result：最后的结果
    string temp = "", result = "";
 
    //对齐A、B
    string part_A(length - A.length(), '0');
    A = part_A + A;
    string part_B(length - B.length(), '0');
    B = part_B + B;
 
    //value_A：读取的A上的每一位值；value_B：读取的B上的每一位值；count：计算是否需要借位
    int value_A, value_B, count = 0;
 
 
    //从低位开始一位一位的累减
    for (int i = 1; i <= length; i++)
    {
        //获取两个整数从尾到头的每个字符
        value_A = String_to_Int(A.substr(length - i, 1));
        value_B = String_to_Int(B.substr(length - i, 1));
 
        //两数相减，不用借位
        if ((value_A - count - value_B) >= 0)
        {
            temp = Int_to_String(value_A - count - value_B);
            result = temp + result;
            count = 0;
        }
        //两数相减，需要借位
        else
        {
            //如果没到最高位，则可以一直借位
            if (i != length)
            {
                temp = Int_to_String(value_A + 10 - count - value_B);
                //表示向前一位借位
                result = temp + result;
                count = 1;
            }
            //到了最高位，只能直接相减
            else
            {
                temp = Int_to_String(value_A - count - value_B);
                result = temp + result;
            }
        }
    }
    return result;
}
 
//大整数乘法
string multiBI(string A, string B)
{
    //获取A、B最长字符串长度
    int lenA = A.length();
    int lenB = B.length();
    int length = lenA > lenB ? lenA : lenB;
 
    //补齐A、B
    if (length == lenA)
    {
        B = string(length - lenB, '0') + B;
    }
    else
    {
        A = string(length - lenA, '0') + A;
    }
 
    if (length ==1)
    {
        return Int_to_String(String_to_Int(A) * String_to_Int(B));;
    }
    else
    {
        string A1, A2, B1, B2;
        A1 = A.substr(0, A.length() / 2 + A.length() % 2);
        A2 = A.substr(A.length() / 2 + A.length() % 2, A.length() / 2);
        B1 = B.substr(0, B.length() / 2 + B.length() % 2);
        B2 = B.substr(B.length() / 2 + B.length() % 2, B.length() / 2);
 
        string A1B1 = multiBI(A1, B1);
        string A2B2 = multiBI(A2, B2);
        string A1B1_plus_A2B2 = plusBI(A1B1, A2B2);
        string A1B2_plus_A2B1 = multiBI(plusBI(A1, A2), plusBI(B1, B2));
        A1B2_plus_A2B1 = subBI(A1B2_plus_A2B1, A1B1_plus_A2B2);
 
        int add_first = A.length() / 2 * 2;
        int add_mid = A.length() / 2;
        for (int i = 0; i < add_first; i++) {
            A1B1 = A1B1 + '0';
        }
        for (int i = 0; i < add_mid; i++) {
            A1B2_plus_A2B1 = A1B2_plus_A2B1 + '0';
        }
        return remove_zero(plusBI(plusBI(A1B1, A1B2_plus_A2B1), A2B2));
    }
}
 
//除去前面的无效0
string remove_zero(string s)
{
    // 除去数字前面的零
    int i = 0;
    while (i < s.length() && s[i] == '0')
    {
        i++;
    }
    if (i < s.length())
    {
        s = s.substr(i);
    }
    else
    {
        s = "0";
    }
    return s;
}
 
//数字转字符串
string Int_to_String(int num)
{
    string outcome;
    stringstream ss;
    ss << num;
    ss >> outcome;
    ss.clear();
    return outcome;
}
 
//字符串转数字
int String_to_Int(string s)
{
    int outcome;
    stringstream ss;
    ss << s;
    ss >> outcome;
    ss.clear();
    return outcome;
}
 
//判断结果正负号
string judge_outcome(string A, string B)
{
    //截取A、B的符号位
    string A_sign = A.substr(0, 1);
    string B_sign = B.substr(0, 1);
    //计数器
    int count = 0;
    if (A_sign == "-")
    {
        count++;
    }
    if (B_sign == "-")
    {
        count++;
    }
    //判断结果
    if (count == 1)
    {
        return "-";
    }
    else
    {
        return "+";
    }
}
 
//补齐符号
string complete_sign(string s)
{
    string temp = s.substr(0, 1);
    if (temp != "-")
    {
        return "+" + s;
    }
    else
    {
        return s;
    }
}
 
int main()
{
    //A、B以字符串的形式存储大整数
    string A, B;
    //判断是否需要继续
    int flag = 1;

    ifstream read_file;
    read_file.open("/mnt/d/myuniversity/Daerxia/algorithm/ReverseClass/test.txt");
    vector<double> vtime;
    struct timeval start, end;

    

    for (int j = 0; j < 5; ++j) {
        vtime.push_back(0);
        for (int i = 0; i < 10; ++i) {
            read_file >> A;
            read_file >> B;
            gettimeofday(&start, NULL);
            multiBI(A, B);
            gettimeofday(&end, NULL);
            vtime[j] = vtime[j] + 1000.0 * (end.tv_sec - start.tv_sec) 
                                + (double) 1.0 * (end.tv_usec - start.tv_usec) / 1000;
        }
        
        vtime[j] = vtime[j] * 1.0 / 10;
    }
    read_file.close();
    cout << endl;
    printf("============================Blow is the time================================\n");
    for (int i = 0; i < vtime.size(); ++i) {
        printf("%.3lf ", vtime[i]);
    }
    printf("\n");

    return 0;
}