import random


def large_rand_num():
    with open('test.txt', 'w') as f:
        content = []
        i = 1
        while i < 100000001:
            if i % 100 == 0:
                print('[randint] still alive ' + str(i))
                f.writelines(content)
                content = []
            for k in range(10):
                print(k, end = ' ')
                s1 = str(random.randint(10 ** (i - 1), (10 ** i) - 1))
                s2 = str(random.randint(10 ** (i - 1), (10 ** i) - 1))
                content.append(s1 + '\n')
                content.append(s2 + '\n')
            print()
            i *= 10
        f.close()
    print("[randint] finished !")
            
       


if __name__ == "__main__":
    large_rand_num()