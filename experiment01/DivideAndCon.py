import re
import pandas as pd
import easydict
import xlrd
import xlwt
from xlutils.copy import copy

config = easydict.EasyDict({
    'fileName': '分治算法实验job.xls',
    'startRow': 3,
    'cntRow': 1,
    'cntCol': 4,
    'numIndex': 2,
    'nameIndex': 3,
    'yingIndex': 4,
    'gaoIndex': 5,
    'daiIndex': 6,
    '汽车学院': 0,
    '材料学院': 1,
    '软件学院': 2,
    '计算机学院': 3,
    '全校线代': 4,
    '全校高数': 5,
    '全校英语': 6,
    '成绩总表': 7,
    'colleges': ['汽车学院', '材料学院', '软件学院', '计算机学院'],
    'ss': {'全校线代成绩表': 'dai', '全校高数成绩表': 'gao', 
            '全校英语成绩表': 'ying', '成绩总表': 'all_included'}
})

class Stu:  
    def __init__(self, faculty: str = '计算机科学与技术学院', 
                num: str = '', name: str = '', 
                ying: float = 0, gao: float = 0, dai: float = 0) -> None:
        self.faculty = faculty
        self.num = num.zfill(6)
        self.name = name
        self.ying = ying
        self.gao = gao
        self.dai = dai
        self.__dict__ = {'name':name, 'num':self.num, 'faculty':faculty,
                        'ying':ying, 'gao':gao, 'dai':dai}
    
    def get(self, attritube:str):
        return self.__dict__[attritube]

    def __str__(self) -> str:
        ans =  self.faculty + '\t' + self.num + '\t' + self.name + '\t'
        ans += str(self.ying) + '\t' + str(self.gao) + '\t' + str(self.dai)
        return ans

class Stulist(list):
    def __init__(self):
        list.__init__([])

    def __str__(self) -> str:
        ans = ''
        for s in self:
            ans += str(s) + '\n'
        return ans

    def append(self, __object: Stu) -> None:
        if type(__object) != type(Stu()):
            print('[warning] Type is not right! Append ' + str(__object) + ' failed!')
        else:
            return super().append(__object)

    def sort(self, subject: str = 'num', flag: bool = 'True'):
        if subject == '':
            return
        self.Quicksort(subject, 0, len(self) - 1)
        if flag:
            self.reverse()

    def Partition(self, subject, l, r):
        t = self[l].get(subject)
        while True:
            while r > l and self[r].get(subject) >= t:
                r -= 1
            while r > l and self[l].get(subject) <= t:
                l += 1
            if l < r:
                self[l], self[r] = self[r], self[l]
            else:
                return l

    def Quicksort(self, subject, l, r):
        if l < r:
            n = self.Partition(subject, l, r)
            self[l], self[n] = self[n], self[l]
            if n - 1 > l:
                self.Quicksort(subject, l, n - 1)
            if n + 1 < r:
                self.Quicksort(subject, n + 1, r)

    def head(self, n: int = 5):
        self.sort('ying')
        head_ying = set()
        for i, s in enumerate(self):
            if i > n:
                break
            head_ying.add(s)
        self.sort('gao')
        head_gao = set()
        for i, s in enumerate(self):
            if i > n:
                break
            head_gao.add(s)
        self.sort('dai')
        head_dai = set()
        for i, s in enumerate(self):
            if i > n:
                break
            head_dai.add(s)
        head_ying = head_ying & head_gao
        head_ying = head_ying & head_dai

        return list(head_ying)

def sheet_dict() -> dict:
    sheet = pd.ExcelFile(config.fileName)
    sheet_dic = {}
    for i, sheet_name in enumerate(sheet.sheet_names):
        sheet_dic[sheet_name] = sheet.parse(sheet_name = sheet_name)
    return sheet_dic

def read_file() -> list:
    sheet = pd.ExcelFile(config.fileName)
    df = []
    for i, sheet_name in enumerate(sheet.sheet_names):
        df.append(sheet.parse(sheet_name = sheet_name))
    return df
 
def get_stu(facultyName : str) -> list:
    df = read_file()
    index = config[facultyName]
    cnt = df[index].iloc[config.cntRow][config.cntCol]
    endRow = cnt + config.startRow
    res = Stulist()
    for i in range(config.startRow, endRow):
        num = df[index].iloc[i][config.numIndex]
        name = df[index].iloc[i][config.nameIndex]
        gao = df[index].iloc[i][config.gaoIndex]
        ying = df[index].iloc[i][config.yingIndex]
        dai = df[index].iloc[i][config.daiIndex]
        res.append(Stu(facultyName, str(num), name, ying, gao, dai))
    return res

def data():
    all_numbers = Stulist()
    dic = {}
    for fac in config.colleges:
        dic[fac] = get_stu(fac)
        for stu in dic[fac]:
            all_numbers.append(stu)
    return all_numbers, dic

def set_style(): 
    '''
    宋体12号 居中
    '''
    style = xlwt.XFStyle()   # initialization pattern
    font = xlwt.Font()
    font.name = '宋体'
    font.height = 0x00F0
    
    style.font = font
    alig = xlwt.Alignment()
    alig.VERT_BOTTOM = xlwt.Alignment.VERT_BOTTOM
    alig.horz = xlwt.Alignment.HORZ_CENTER
    style.alignment = alig
    return style

def border_style():
    borders = xlwt.Borders()
    borders.left = 1
    borders.right = 1
    style = xlwt.XFStyle()
    style.borders = borders
    return style

def write_file(flag: bool = True, subject = 'gao', sheet_name = '汽车学院'):
    '''
    flag为true就是前面的数据排序,
    false是后面几个sheet的填充. 
    若flag true 则subject sheet_name需要填,
    若为false 则只需要填sheet_name .
    '''
    school, college = data()
    old = xlrd.open_workbook(config.fileName, formatting_info = True)
    newfile = copy(old)
    ws = newfile.get_sheet(sheet_name)
    if flag:
        faculty = sheet_name.split('学院')[0] + '学院'
        college[faculty].sort(subject)
        cnt = len(college[faculty])
        for i in range(config.startRow, config.startRow + cnt):
            ws.write(i + 1, config.numIndex, college[faculty][i - config.startRow].get('num'), set_style())
            ws.write(i + 1, config.nameIndex, college[faculty][i - config.startRow].get('name'), set_style())
            ws.write(i + 1, config.yingIndex, college[faculty][i - config.startRow].get('ying'), set_style())
            ws.write(i + 1, config.gaoIndex, college[faculty][i - config.startRow].get('gao'), set_style())
            ws.write(i + 1, config.daiIndex, college[faculty][i - config.startRow].get('dai'), set_style())

    else:
        if config.ss[sheet_name] == 'all_included':
            all_included = school.head()
            for i, s in enumerate(all_included):
                ws.write(i + 2, 0, s.get('num'), set_style())
                ws.write(i + 2, 1, s.get('name'), set_style())
                ws.write(i + 2, 2, s.get('faculty'), set_style())
                ws.write(i + 2, 3, s.get('ying'), set_style())
                ws.write(i + 2, 4, s.get('gao'), set_style())
                ws.write(i + 2, 5, s.get('dai'), set_style())
        else:
            subject = config.ss[sheet_name]
            school.sort(subject)
            for i in range(5):
                ws.write(i + 2, 0, school[i].get('faculty'), set_style())
                ws.write(i + 2, 1, school[i].get('num'), set_style())
                ws.write(i + 2, 2, school[i].get('name'), set_style())
                ws.write(i + 2, 3, school[i].get(subject), set_style())
        
    newfile.save(config.fileName)

if __name__ == "__main__":
    write_file()
    write_file(sheet_name = '材料学院', subject = 'gao')
    write_file(sheet_name = '软件学院', subject = 'dai')
    write_file(sheet_name = '计算机学院', subject = 'ying')
    write_file(False, sheet_name = '全校线代成绩表')
    write_file(False, sheet_name = '全校高数成绩表')
    write_file(False, sheet_name = '全校英语成绩表')
    write_file(False, sheet_name = '成绩总表')
