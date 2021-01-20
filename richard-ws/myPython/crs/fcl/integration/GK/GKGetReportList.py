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

def getReportList(gkToken):
    url = 'https://storeapi.grocerkeystaging.com/report'

    headers = {
                'Host' : 'storeapi.grocerkeystaging.com',
                'Content-Type' : 'application/json',
                'StoreCode' : '201',
                'Auth_Token' : gkToken,
            }

    payload = {
            }

    #response = requests.post(url, data=payload, headers=headers, verify=False)
    #response = requests.post(url, data=payload, headers=headers)
    response = requests.get(url, headers=headers)
    return response

# Main process
gkToken = getAccessToken()
#print ( gkToken)
resp = getReportList(gkToken)
body = json.loads(resp.content)
print (json.dumps(body, indent=2, sort_keys=True))

