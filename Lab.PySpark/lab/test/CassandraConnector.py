'''
Created on May 16, 2020

@author: kntran
'''

from cassandra.cluster import Cluster
from cassandra.auth import PlainTextAuthProvider



def connectToCassandra():
    auth_provider = PlainTextAuthProvider(username='cassandra', password='welcome123')
    cluster = Cluster(["10.11.1.1"],auth_provider = auth_provider)
    session = cluster.connect()
    session.set_keyspace('lab')
    cluster.connect()
    rows = session.execute("SELECT content  FROM document_pdf where file_name = 'UTResume.pdf.txt'")
    for row in rows:
        print(row.content.encode('utf-8'))
        
def text():
    print('Hello World')        

if __name__ == '__main__':
    connectToCassandra()