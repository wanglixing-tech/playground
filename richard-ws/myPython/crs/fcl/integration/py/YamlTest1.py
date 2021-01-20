'''
Created on Mar 4, 2020

@author: Richard.Wang
'''
import yaml

if __name__ == '__main__':

    stream = open(r'C:\Users\Richard.Wang\WS-HOME\ws-python\myPython\categories.yaml', 'r')
    dictionary = yaml.load(stream, Loader=yaml.FullLoader)
    for key, value in dictionary.items():
        iList = list(value)
        print (key + " : ")     
        for i in iList:
            print ("\t" + i)   