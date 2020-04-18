'''
Created on Feb 9, 2020

@author: kntran
'''

import pyspark
import time
from pyspark.context import SparkContext

#os.environ['PYSPARK_PYTHON'] = '/usr/bin/python'


conf = pyspark.SparkConf()
conf.setMaster('spark://144.91.109.48:7077')
conf.set('spark.authenticate', False)
conf.set('spark.driver.host','10.8.0.2')
conf.set('spark.executor.memory', "2g")
conf.setAppName('Python - ' +time.strftime('%X %x %Z'))
sc = SparkContext(conf=conf)

def simpleSparkTask1():
    big_list = range(10000)
    rdd = sc.parallelize(big_list, 1)
    odds = rdd.filter(lambda x: x % 2 != 0)
    print(odds.take(5))
    print('done')
    
def simpleSparkTask2():
    file = "file:///c:/tmp/csv1.csv"
    sc.addFile(file)
    txt = sc.textFile("csv1.csv");
    print(txt.count())
    



if __name__ == '__main__':
    simpleSparkTask2()