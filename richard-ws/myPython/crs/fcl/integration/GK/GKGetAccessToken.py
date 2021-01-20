'''
Created on Jun 26, 2020

@author: Richard.Wang
'''
import requests
import json
from basicauth import encode

def getAccessToken():   
    url = 'https://storeapi.grocerkeystaging.com/authentication'
    headers = {
                'Content-Type' : 'application/json',
                'Accept' : '*/*',
                'StoreCode' : '201'
            }
    payload = {
                "Email" : 'navin.khanna@fcl.crs',
                "Password" : '5u1=wU#D',
            }

    s = requests.Session()
    #response = requests.post(url, data=payload, headers=headers, verify=False)
    response = s.post(url, json=payload, headers=headers)
    body = json.loads(response.content)
    print ( response)
    token = body["Token"]
    return token

gkToken = getAccessToken()
print ( gkToken)
