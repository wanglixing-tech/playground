'''
Created on Mar 5, 2020

@author: Richard.Wang
'''
import yaml
if __name__ == '__main__':

    dict_file = [{'sports' : ['soccer', 'football', 'basketball', 'cricket', 'hockey', 'table tennis']},
                 {'countries' : ['Pakistan', 'USA', 'India', 'China', 'Germany', 'France', 'Spain']}]

    with open(r'C:\Temp\store_file.yaml', 'w') as file:
        documents = yaml.dump(dict_file, file)