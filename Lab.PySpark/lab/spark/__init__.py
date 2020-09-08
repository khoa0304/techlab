import time

from cassandra.auth import PlainTextAuthProvider
from cassandra.cluster import Cluster
import pyspark
from pyspark.context import SparkContext
from pyspark.sql import SQLContext

import pandas as pd


#import com.datastax.spark.connector._
#os.environ['PYSPARK_PYTHON'] = '/usr/bin/python'
