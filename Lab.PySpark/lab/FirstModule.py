import json

with open('SampleSubjectLinePayload.json') as f:
    # parse x:
    y = json.load(f)

# the result is a Python dictionary:
print("I am printing raw data source")
print(y["dataSource"])

print("Now lets do the formatting ")


for item in y["dataSource"]:
    print("dbUser: {}\ndbURL: {}\ndbPassword: {}\ndbName: {}\n".format(item['dbUser'],item['dbURL'],item['dbPassword'],item['dbName']))
    

print("Printing modelStoredTableName  and DB_IDENTIFIER\n")

modelStoredtableNames = json.dumps(y["modelStoredTableName"]["tableNames"])
modelStoreddbNames = json.dumps(y["modelStoredTableName"]["dbNames"])
#print(modelStoredTableName)
for tName in y["modelStoredTableName"]["tableNames"]:
    print(tName)
for dbName in y["modelStoredTableName"]["dbNames"]:
    print(dbName)   
