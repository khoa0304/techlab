'''
Created on Feb 9, 2020

@author: kntran
'''

import pyspark
import time
#import com.datastax.spark.connector._

from pyspark.context import SparkContext
from pyspark.sql import SQLContext
#os.environ['PYSPARK_PYTHON'] = '/usr/bin/python'
from cassandra.cluster import Cluster
from cassandra.auth import PlainTextAuthProvider
import pandas as pd

conf = pyspark.SparkConf()

conf.setMaster('spark://144.91.109.48:7077')
conf.set('spark.authenticate', False)
conf.set('spark.driver.host','10.8.0.2')
conf.set('spark.driver.port','10802')
#conf.set('spark.ui.port','80802')
conf.set('spark.executor.memory', "4g")
conf.setAppName('Python - ' +time.strftime('%X %x %Z'))
conf.set("spark.cassandra.connection.host","10.11.1.1")
conf.set("spark.cassandra.auth.username", "cassandra")            
conf.set("spark.cassandra.auth.password", "welcome123")

sc = SparkContext(conf=conf)
sqlContext = SQLContext(sc)

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
    
    
def connectToCassandra():
    auth_provider = PlainTextAuthProvider(username='cassandra', password='welcome123')
    cluster = Cluster(["10.11.1.1"],auth_provider = auth_provider)
    session = cluster.connect()
    session.set_keyspace('lab')
    return session
    
    #for row in rows:
     #   print(row.content.encode('utf-8'))
        
        
#https://github.com/datastax/spark-cassandra-connector/blob/master/doc/15_python.md
def load_and_get_table_df(keys_space_name, table_name):
    table_df = sqlContext.read\
        .format("org.apache.spark.sql.cassandra")\
        .options(table=table_name, keyspace=keys_space_name)\
        .load()
    return table_df

def readFromCassandraUsingPandas():
    session = connectToCassandra();
    rows = session.execute("SELECT content  FROM document_pdf where file_name = 'UTResume.pdf.txt'")
    df = pd.DataFrame(rows)
    df.to_csv('c://tmp//text.csv',sep=';',mode='w',index = False, header=True)
    df = pd.read_csv('c://tmp//text.csv')
      
    print(df)
    session.shutdown();
    
def readFromCassandraUsingSQLContext():
    table_df = load_and_get_table_df("lab","document_pdf")
    table_df.show()
    
if __name__ == '__main__':
    readFromCassandraUsingPandas()