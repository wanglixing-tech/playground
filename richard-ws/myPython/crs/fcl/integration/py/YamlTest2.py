'''
Created on Mar 5, 2020

@author: Richard.Wang
'''
import yaml
if __name__ == '__main__':


    with open(r'C:\Users\Richard.Wang\WS-HOME\ws-python\myPython\categories.yaml') as file:
        # The FullLoader parameter handles the conversion from YAML
        # scalar values to Python the dictionary format
        fruits_list = yaml.load(file, Loader=yaml.FullLoader)

        print(fruits_list)