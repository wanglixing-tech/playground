'''
Created on Jun 26, 2020

@author: Richard.Wang
'''
import requests
import shutil
import json
from basicauth import encode
import xlsxwriter

def getAccessTokenAndStoreID():   
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
    storeID = body["InitialLoadDataObj"]["Store"]["StoreID"]
    return ( token, storeID )

def createReportByReportIDAndStoreID(gkToken, reportID, storeID):
    print ( "token=" + gkToken )
    print ( "reportID=" + reportID )
    print ( "storeID=" + storeID )
    url = 'https://storeapi.grocerkeystaging.com/v2/report/run'

    headers = {
                'Host' : 'storeapi.grocerkeystaging.com',
                'Content-Type' : 'application/json',
                'StoreCode' : '201',
                'Auth_Token' : gkToken,
            }

    payload = {
                "ReportID": 40,
                "StoreIDs": [storeID],
                "LoggedinUserID": 0,
                "PermittedStoreIDs": [storeID],
                "StoreID": storeID,
                "UserID": 0
            }
    response = requests.post(url, json=payload, headers=headers)
    return response

# Main process
(gkToken, storeID) = getAccessTokenAndStoreID()
print ( gkToken )
print ( storeID )
reportFileName = "C:\\Temp\\report-ID-40.xlsx"
resp = createReportByReportIDAndStoreID(gkToken, "40", storeID)
returnCode = resp.status_code
#del resp
with open(reportFileName, 'wb') as outf:
    outf.write(resp.content)
print ( returnCode )
