'''
Created on May 17, 2020

@author: kntran
'''

from py4j.java_gateway import JavaGateway

def main():
    jvm = JavaGateway().jvm
    myadd = jvm.com.littlely.AddNum()
    print(myadd.add(40.0, 80.0))


if __name__ == "__main__":
    main()